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
	private static final String ATTACK_MONSTER = "2_3";
//	private static final String MONSTER_ATTACK_USER = "2_4";
//	private static final String CHAT_TO_NPC = "2_5";

	public static final String NOTIFY_USER_ENTER = "2_100";
	public static final String NOTIFY_USER_LEAVE = "2_101";
	public static final String NOTIFY_USER_MONSTER_BE_KILLED = "2_102";
	public static final String NOTIFY_MONSTER_DIED = "2_103";
	public static final String NOTIFT_USER_DIED = "2_104";
	public static final String ATTACK_STATUS_CHANGE = "2_105";
//	private static final String USER_STATUS_CHANGE = "2_106";

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

	@CmdService(cmd = ATTACK_MONSTER)
	public void attackMonster(User user, CMDdomain cmdDomain) {
		int skillId = cmdDomain.getIntParam("ski");
		int monsterId = cmdDomain.getIntParam("mi");

		ReplyDomain replyDomain = service.attackMonster(user, skillId, monsterId);

		replyDomain.setStringDomain("cmd", ATTACK_MONSTER);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		replyDomain.setIntDomain("uid", user.getUserId());
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	} 

}
