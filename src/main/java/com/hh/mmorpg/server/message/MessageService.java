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
	 * 	其实如果发送的人在不同的服务器的话就需要用到RPC
	 * 
	 */
	public ReplyDomain sendWorldMessage(String content) {
		// TODO Auto-generated method stub
		Map<Integer, Scene> sceneMap = SceneService.INSTANCE.getSceneMap();
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", MessageExtension.NOTIFY_USER_WORLD_MESSAGE);
		replyDomain.setStringDomain("c", content);
		for (Scene scene : sceneMap.values()) {
			scene.notifyAllUser(replyDomain);
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain sendMessageToUser(User user, int userId, String content) {
		User recipentUser = UserService.INSTANCE.getUser(userId);
		if (recipentUser == null) {
			return ReplyDomain.FAILE;
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		ReplyDomain message = new ReplyDomain();
		message.setIntDomain("rid", role.getRoleId());
		message.setStringDomain("name", role.getName());
		message.setStringDomain("content", content);

		return ReplyDomain.SUCCESS;
	}

}
