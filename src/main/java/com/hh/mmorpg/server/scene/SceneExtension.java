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
	private static final String GET_USER_SCENE = "2_2";

	public static final String NOTIFY_USER_ENTER = "2_100";
	public static final String NOTIFY_USER_LEAVE = "2_101";

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user, CMDdomain cmdDomain) {
		int oldSceneId = cmdDomain.getIntParam("osi");
		int sceneId = cmdDomain.getIntParam("si");

		ReplyDomain replyDomain = service.userJoinScene(user, oldSceneId, sceneId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUserEnter(User user, String cmd) {
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", cmd);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
