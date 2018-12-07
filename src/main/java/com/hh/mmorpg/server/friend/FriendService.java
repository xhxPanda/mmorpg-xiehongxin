package com.hh.mmorpg.server.friend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.hh.mmorpg.domain.Friend;
import com.hh.mmorpg.domain.FriendApply;
import com.hh.mmorpg.domain.OccupationEmun;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.FriendData;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleDao;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.service.user.UserService;

public class FriendService {

	public static final FriendService INSTANCE = new FriendService();

	private static final int FRIEND_LIMIT_NUM = 10;

	// 申请列表
	private ConcurrentHashMap<Integer, Map<Integer, FriendApply>> friendApplyMap;

	private Lock lock;

	// 热点数据，用户角色数据缓存
	private ConcurrentHashMap<Integer, Map<Integer, Friend>> cache;

	private FriendService() {
		this.friendApplyMap = new ConcurrentHashMap<>();
		this.cache = new ConcurrentHashMap<>();
		this.lock = new ReentrantLock();
	}

	public ReplyDomain getRoleFriends(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Map<Integer, Friend> map = cache.get(role.getId());

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("好友列表", map.values());
		return replyDomain;
	}

	/**
	 * 申请添加好友
	 * 
	 * @param user
	 * @param roleId
	 * @param content
	 * @return
	 */
	public ReplyDomain applyAddFriend(User user, int roleId, String content) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Map<Integer, Friend> friendMap = cache.get(role.getId());

		if (friendMap != null && friendMap.size() >= FRIEND_LIMIT_NUM) {
			return ReplyDomain.FRIEND_FULL;
		}

		if (getFriendApply(role.getId()).containsKey(roleId)) {
			return ReplyDomain.HAS_SENT_APPLY;
		}

		FriendApply apply = new FriendApply(roleId, role.getId(), role.getName(), role.getLevel(), content,
				user.getUserId());

		addApply(roleId, apply);
		// 持久化申请的信息
		FriendDao.INSTANCE.insertFriendApply(apply);

		// 如果对方在线，通知对方
		if (RoleService.INSTANCE.isOnline(roleId)) {
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("名称", role.getName());
			replyDomain.setStringDomain("请求内容", content);
			replyDomain.setStringDomain("cmd", FriendExtension.NOTIFY_FRIEND_APPLY);

			User appUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
			FriendExtension.notifyUser(appUser, replyDomain);
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 处理好友申请
	 * 
	 * @param user
	 * @param roleId
	 * @param agree
	 * @return
	 */
	public ReplyDomain dealFriendApply(User user, int roleId, boolean agree) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Map<Integer, FriendApply> map = friendApplyMap.get(role.getId());
		if (!agree) {
			// 最后删除请求
			FriendDao.INSTANCE.deleteFriendApply(role.getId(), roleId);
			map.remove(roleId);
		}

		if (map == null || map.get(roleId) == null) {
			return ReplyDomain.FAILE;
		}

		FriendApply friendApply = map.get(roleId);

		// 从检测对方的好友列表的时候加锁，因为这里有可能有并发问题
		lock.lock();

		try {
			Map<Integer, Friend> friendMap = cache.get(role.getId());
			if (friendMap != null && friendMap.size() >= FRIEND_LIMIT_NUM) {
				return ReplyDomain.FRIEND_FULL;
			}

			Map<Integer, Friend> applyRoleMap = getUserAllFriend(roleId);
			if (applyRoleMap != null && applyRoleMap.size() >= FRIEND_LIMIT_NUM) {
				return ReplyDomain.FRIEND_FULL;
			}

			// 生成自己的friend对象
			Friend friend = new Friend(role.getId(), roleId, System.currentTimeMillis());
			assemblingFriend(friend);
			// 更新缓存
			if (friendMap == null) {
				friendMap = new HashMap<>();
				cache.put(role.getId(), friendMap);
			}
			friendMap.put(friend.getFriendId(), friend);

			// 生成对方的friend对象
			Friend applyRolefriend = new Friend(roleId, role.getId(), System.currentTimeMillis());
			assemblingFriend(applyRolefriend);
			if (applyRoleMap == null) {
				applyRoleMap = new HashMap<>();
				cache.put(role.getId(), applyRoleMap);
			}
			applyRoleMap.put(applyRolefriend.getFriendId(), applyRolefriend);
			// 两者都需要持久化
			FriendDao.INSTANCE.insertFriend(friend);
			FriendDao.INSTANCE.insertFriend(applyRolefriend);

			// 如果在线，发送notify，并更新缓存
			if (RoleService.INSTANCE.isOnline(roleId)) {
				applyRoleMap.put(applyRolefriend.getFriendId(), applyRolefriend);

				ReplyDomain notify = new ReplyDomain();
				notify.setStringDomain("cmd", FriendExtension.NOTIFY_FRIEND_APPLY_PASS);
				notify.setStringDomain("名称", role.getName());

				User applyRoleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
				FriendExtension.notifyUser(applyRoleUser, notify);
			}

			// 抛出加好友事件
			FriendData friendData = new FriendData(role, roleId);

			Role applyRole = RoleService.INSTANCE.getUserRole(friendApply.getUserId(), roleId);
			FriendData applyFriendData = new FriendData(applyRole, role.getId());

			EventHandlerManager.INSATNCE.methodInvoke(EventType.BECOME_FRIEND,
					new EventDealData<FriendData>(friendData));
			EventHandlerManager.INSATNCE.methodInvoke(EventType.BECOME_FRIEND,
					new EventDealData<FriendData>(applyFriendData));

		} finally {
			lock.unlock();
		}

		// 最后删除请求
		FriendDao.INSTANCE.deleteFriendApply(role.getId(), roleId);
		map.remove(roleId);
		return ReplyDomain.SUCCESS;
	}

	private void addApply(int roleId, FriendApply friendApply) {
		Map<Integer, FriendApply> map = friendApplyMap.get(roleId);

		map.put(friendApply.getRoleId(), friendApply);
	}

	/**
	 * 删除好友
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain deleteFriend(User user, int roleId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Map<Integer, Friend> friends = cache.get(role.getId());

		if (friends == null || friends.containsKey(roleId)) {
			return ReplyDomain.FAILE;
		}

		// 删除缓存中的
		friends.remove(roleId);

		// 删除持久层
		FriendDao.INSTANCE.deleteFriend(role.getId(), roleId);
		FriendDao.INSTANCE.deleteFriend(roleId, role.getId());

		// 在线的话删除缓存中的好友
		if (RoleService.INSTANCE.isOnline(roleId)) {
			Map<Integer, Friend> roleFriends = getUserAllFriend(roleId);
			// 删除缓存
			roleFriends.remove(roleId);

		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 获取好友申请列表
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain getFriendsApplication(User user) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("好友申请列表", friendApplyMap.get(role.getId()).values());

		return replyDomain;
	}

	/**
	 * 构建好友的信息
	 * 
	 * @param friend
	 */
	private void assemblingFriend(Friend friend) {
		int friendId = friend.getFriendId();

		Role role = null;
		boolean isOnline = false;
		if (RoleService.INSTANCE.isOnline(friendId)) {
			role = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(friendId), friendId);

			isOnline = true;
		} else {
			role = RoleDao.INSTANCE.selectRole(friendId);
		}

		friend.setFriendLevel(role.getLevel());
		friend.setFriendName(role.getName());
		friend.setFriendOccupation(OccupationEmun.getOccupationEmun(role.getOccupationId()).getName());
		friend.setOnline(isOnline);
	}

	/**
	 * 获取好友请求，如果缓存中没有就从数据库中获取
	 * @param roleId
	 * @return
	 */
	private Map<Integer, FriendApply> getFriendApply(int roleId) {
		Map<Integer, FriendApply> map = friendApplyMap.get(roleId);

		if (map == null) {
			map = new HashMap<>();
			friendApplyMap.put(roleId, map);
		}
		
		List<FriendApply> friendApplies = FriendDao.INSTANCE.getRoleFriendsApply(roleId);
		for(FriendApply friendApply : friendApplies) {
			map.put(friendApply.getApplyRoleId(), friendApply);
		}
		
		return map;
	}

	/**
	 * 获取用户好友的列表
	 * 
	 * @param roleId
	 * @return
	 */
	private Map<Integer, Friend> getUserAllFriend(Integer roleId) {
		// TODO Auto-generated method stub

		Map<Integer, Friend> map = new HashMap<>();
		if (cache.containsKey(roleId)) {
			map = cache.get(roleId);
		} else {
			List<Friend> friends = FriendDao.INSTANCE.getRoleFriends(roleId);
			if (friends == null || friends.size() == 0) {
				return map;
			} else {
				map = friends.stream().collect(Collectors.toMap(Friend::getRoleId, a -> a));
			}
		}

		for (Friend friend : map.values()) {
			assemblingFriend(friend);
		}

		return map;
	}

	// 用户使用角色后把旧角色的缓存移除，加入新角色的缓存
	@Event(eventType = EventType.ROLE_CHANGE)
	public void handleRoleChange(EventDealData<RoleChangeData> data) {

		Role oldRole = data.getData().getOldRole();
		if (oldRole != null) {
			friendApplyMap.remove(oldRole.getId());
			cache.remove(oldRole.getId());
		}

		Role role = data.getData().getNewRole();

		// 新角色的申请列表加入缓存
		List<FriendApply> applies = FriendDao.INSTANCE.getRoleFriendsApply(role.getId());
		Map<Integer, FriendApply> appliesMap = applies.stream()
				.collect(Collectors.toMap(FriendApply::getApplyRoleId, a -> a));
		friendApplyMap.put(role.getId(), appliesMap);

		// 好友缓存
		cache.put(role.getId(), getUserAllFriend(role.getId()));
	}

}
