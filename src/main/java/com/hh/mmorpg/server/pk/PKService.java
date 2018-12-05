package com.hh.mmorpg.server.pk;

import java.util.concurrent.locks.ReentrantLock;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.PKData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class PKService {

	public static final PKService INSATNCE = new PKService();

	private ReentrantLock lock;

	private PKService() {
		lock = new ReentrantLock();
	}

	/**
	 * 邀请pk
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain invitePK(User user, int roleId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Scene scene = SceneService.INSTANCE.getUserScene(user.getUserId());

		// 不在场景中或者该场景不能pk皆是错误
		if (scene == null || scene.isCanBattle()) {
			return ReplyDomain.FAILE;
		}

		// 对方不在线也不能邀请
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User otherUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
		Scene otherScene = SceneService.INSTANCE.getUserScene(otherUser.getUserId());

		// 不在同一个场景不能邀请
		if (otherScene == null || otherScene.getId() != scene.getId()) {
			return ReplyDomain.FAILE;
		}

		Role otherRole = RoleService.INSTANCE.getUserUsingRole(otherUser.getUserId());
		if (otherRole.getPkRoleId() != 0 || role.getPkRoleId() != 0) {
			return ReplyDomain.FAILE;
		}

		ReplyDomain notify = new ReplyDomain();
		notify.setStringDomain("cmd", pkExtension.NOTIFY_ROLE_INVITED_PK);
		notify.setStringDomain("对方名称", role.getName());
		notify.setIntDomain("对方等级", role.getLevel());
		pkExtension.notifyUserMessage(otherUser, notify);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 同意pk请求
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain dealPKApply(User user, int roleId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Scene scene = SceneService.INSTANCE.getUserScene(user.getUserId());

		// 不在场景中或者该场景不能pk皆是错误
		if (scene == null || scene.isCanBattle()) {
			return ReplyDomain.FAILE;
		}

		// 对方不在线
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User otherUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
		Scene otherScene = SceneService.INSTANCE.getUserScene(otherUser.getUserId());

		// 不在同一个场景不能邀请
		if (otherScene == null || otherScene.getId() != scene.getId()) {
			return ReplyDomain.FAILE;
		}

		// 在这里上锁保证对方只能与一个人进行pk
		lock.lock();
		try {
			Role otherRole = RoleService.INSTANCE.getUserUsingRole(otherUser.getUserId());
			if (otherRole.getPkRoleId() != 0 || role.getPkRoleId() != 0) {
				return ReplyDomain.FAILE;
			}

			// 相互设置对方为pk对象
			role.setPkRoleId(otherRole.getId());
			otherRole.setPkRoleId(role.getId());

			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", pkExtension.NOTIFY_ROLE_JOIN_PK);
			replyDomain.setStringDomain("对方名称", role.getName());
			replyDomain.setIntDomain("对方等级", role.getLevel());
			pkExtension.notifyUserMessage(otherUser, replyDomain);

		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * pk结束，清除角色的pk状态
	 * 
	 * @param data
	 */
	@Event(eventType = EventType.PK)
	public void handleUsertalkToNpc(EventDealData<PKData> data) {
		PKData npcTalkData = data.getData();

		int winRoleId = npcTalkData.getWinRoleId();
		int loseRoleId = npcTalkData.getLoseRoleId();

		Role winRole = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(winRoleId), winRoleId);
		Role loseRole = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(loseRoleId), loseRoleId);

		winRole.setPkRoleId(0);
		loseRole.setPkRoleId(0);
	}
}
