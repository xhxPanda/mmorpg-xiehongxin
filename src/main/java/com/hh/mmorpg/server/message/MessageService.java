package com.hh.mmorpg.server.message;

import java.util.Map;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class MessageService {

	public static final MessageService INSTANCE = new MessageService();

	/**
	 * 其实如果发送的人在不同的服务器的话就需要用到RPC
	 * 
	 */
	public ReplyDomain sendWorldMessage(String content) {
		Map<Integer, Scene> sceneMap = SceneService.INSTANCE.getSceneMap();
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", MessageExtension.NOTIFY_USER_WORLD_MESSAGE);
		replyDomain.setStringDomain("内容", content);
		for (Scene scene : sceneMap.values()) {
			scene.notifyAllUser(replyDomain);
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain sendMessageToUser(User user, int roleId, String content) {

		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User recipentUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));

		if (recipentUser == null) {
			return ReplyDomain.FAILE;
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		ReplyDomain message = new ReplyDomain();
		message.setIntDomain("角色id", role.getId());
		message.setStringDomain("角色名", role.getName());
		message.setStringDomain("内容", content);
		MessageExtension.notifyUserMessage(recipentUser, message);
		return ReplyDomain.SUCCESS;
	}

}
