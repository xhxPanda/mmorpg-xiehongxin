package com.hh.mmorpg.server.role;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;

public class RoleService {

	public static final RoleService INSTANCE = new RoleService();

	// 热点数据，用户角色数据缓存
	private LoadingCache<Integer, Map<Integer, Role>> cache = CacheBuilder.newBuilder()
			.refreshAfterWrite(10, TimeUnit.MINUTES).expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(1000)
			.build(new CacheLoader<Integer, Map<Integer, Role>>() {
				@Override
				/** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
				public Map<Integer, Role> load(Integer appKey) {
					return getUserAllRole(appKey);
				}

			});

	private ConcurrentHashMap<Integer, Role> userRoleMap;

	private RoleService() {
		userRoleMap = new ConcurrentHashMap<>();

		EventHandlerManager.INSATNCE.register(this);
	}

	public ReplyDomain getAllRole(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Map<Integer, Role> map = getUserAllRole(userId);
		ReplyDomain replyDomain = ReplyDomain.SUCCESS;
		replyDomain.setListDomain("rs", map.values());
		return replyDomain;
	}

	public ReplyDomain userUseRole(User user, int roleId) {
		int userId = user.getUserId();
		Role role = getUserRole(userId, roleId);
		if (role == null) {
			return ReplyDomain.FAILE;
		}

		if (userRoleMap.get(userId) != null) {
			return ReplyDomain.FAILE;
		}
		userRoleMap.put(userId, role);

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain getUserUsingRole(User user) {
		ReplyDomain replyDomain = ReplyDomain.SUCCESS;
		Role role = getUserUsingRole(user.getUserId());
		if (role != null) {
			replyDomain.setStringDomain("role", role.toString());
		} else {
			replyDomain.setStringDomain("role", "");
		}
		return replyDomain;
	}

	public Role getUserUsingRole(int userId) {
		Role role = userRoleMap.get(userId);
		return role;
	}

	public ReplyDomain creatRole(User user, int roleDomainId, String name) {
		// TODO Auto-generated method stub
		int id = IncrementManager.INSTANCE.increase("role");
		Role role = new Role(user.getUserId(), id, name, roleDomainId);
		int i = RoleDao.INSTANCE.insertRole(role);
		if (i < 0) {
			return ReplyDomain.FAILE;
		}

		getUserAllRole(user.getUserId()).put(role.getId(), role);
		return ReplyDomain.SUCCESS;
	}

	private Role getUserRole(int userId, int roleId) {
		try {
			return cache.get(userId).get(roleId);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Map<Integer, Role> getUserAllRole(int userId) {

		List<Role> roles = RoleDao.INSTANCE.selectUserRole(userId);
		Map<Integer, Role> map = roles.stream().collect(Collectors.toMap(Role::getId, a -> a));

		return map;
	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		
		int userId = userLostData.getUser().getUserId();
		userRoleMap.remove(userId);
		System.out.println("删除用户缓存使用角色");
	}

}