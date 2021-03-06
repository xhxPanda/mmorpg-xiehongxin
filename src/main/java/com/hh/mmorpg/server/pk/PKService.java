package com.hh.mmorpg.server.pk;

import java.util.concurrent.locks.ReentrantLock;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.PKData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class PKService {

	public static final PKService INSATNCE = new PKService();

	private ReentrantLock lock;

	private PKService() {
		lock = new ReentrantLock();

		EventHandler.INSTANCE.addHandler(PKData.class, pkBuilder);
		EventHandler.INSTANCE.addHandler(UserLostData.class, userLostEvent);
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
		if (scene == null || !scene.isCanBattle()) {
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
		notify.setStringDomain("cmd", PKExtension.NOTIFY_ROLE_INVITED_PK);
		notify.setStringDomain("对方名称", role.getName());
		notify.setIntDomain("对方等级", role.getLevel());
		PKExtension.notifyUserMessage(otherUser, notify);

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
		if (scene == null || !scene.isCanBattle()) {
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
			replyDomain.setStringDomain("cmd", PKExtension.NOTIFY_ROLE_JOIN_PK);
			replyDomain.setStringDomain("对方名称", role.getName());
			replyDomain.setIntDomain("对方等级", role.getLevel());
			PKExtension.notifyUserMessage(otherUser, replyDomain);
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
	EventBuilder<PKData> pkBuilder = new EventBuilder<PKData>() {

		@Override
		public void handler(PKData pkData) {

			int winRoleId = pkData.getWinRoleId();
			int loseRoleId = pkData.getLoseRoleId();

			Role winRole = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(winRoleId), winRoleId);
			Role loseRole = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(loseRoleId), loseRoleId);

			winRole.setPkRoleId(0);
			loseRole.setPkRoleId(0);

			// 如果pk结束了就给双方发出提示
			if (RoleService.INSTANCE.isOnline(winRoleId)) {
				User winUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(winRoleId));
				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("cmd", PKExtension.NOTIFY_ROLE_PK_WIN);
				PKExtension.notifyUserMessage(winUser, replyDomain);

			}

			if (RoleService.INSTANCE.isOnline(loseRoleId)) {
				User loseUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(loseRoleId));
				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("cmd", PKExtension.NOTIFY_ROLE_PK_LOSE);
				PKExtension.notifyUserMessage(loseUser, replyDomain);
			}

			// 抛出事件
			PKData data = new PKData(winRoleId, loseRoleId);
			EventHandler.INSTANCE.invodeMethod(PKData.class, data);
		}

	};

	/**
	 * pk中途掉线了，判定对方胜利并消除双方的pk状态
	 */
	private EventBuilder<UserLostData> userLostEvent = new EventBuilder<UserLostData>() {

		@Override
		public void handler(UserLostData userLostData) {
			Role role = userLostData.getRole();

			if (role == null || role.getPkRoleId() == 0) {
				return;
			}

			// 还原pkid
			role.setPkRoleId(0);

			int pkRoleId = role.getPkRoleId();

			// 判定对方为赢

			User winUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(pkRoleId));
			if (winUser != null) {
				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("cmd", PKExtension.NOTIFY_ROLE_PK_WIN);
				PKExtension.notifyUserMessage(winUser, replyDomain);
			}

			// 抛出事件
			PKData data = new PKData(pkRoleId, role.getId());
			EventHandler.INSTANCE.invodeMethod(PKData.class, data);
		}
	};
}
