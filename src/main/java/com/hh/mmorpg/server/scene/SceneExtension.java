package com.hh.mmorpg.server.scene;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 2)
public class SceneExtension {

	private static SceneService service = SceneService.INSTANCE;

	private static final String JOIN_SCENE = "2_1";
	private static final String GET_SCENE_USER = "2_2";
	private static final String CHAT_TO_NPC = "2_3";
	private static final String KILL_MONSTER = "2_4";

	public static final String NOTIFY_USER_ENTER = "2_100";
	public static final String NOTIFY_USER_LEAVE = "2_101";
	public static final String NOTIFY_USER_MONSTER_BE_KILLED = "2_102";

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user, CMDdomain cmdDomain) {
		int sceneId = cmdDomain.getIntParam("si");

		ReplyDomain replyDomain = service.userJoinScene(user, sceneId);
		replyDomain.setStringDomain("cmd", JOIN_SCENE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_SCENE_USER)
	public void getSceneUser(User user, CMDdomain cmdDomain) {
		ReplyDomain replyDomain = service.getSeceneUser(user);

		replyDomain.setStringDomain("cmd", GET_SCENE_USER);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, int userId, String cmd) {
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", cmd);
		replyDomain.setIntDomain("uid", userId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUserMonsterBeKilled(User user, int userId, int monsterId) {
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", NOTIFY_USER_MONSTER_BE_KILLED);
		replyDomain.setIntDomain("uid", userId);
		replyDomain.setIntDomain("mid", monsterId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
