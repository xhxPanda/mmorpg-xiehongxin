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
	private static final String ATTACK_ROLE = "2_4";
	private static final String GET_ROLE_KILL_MONSTER_BONUS_INFO = "2_5";
	private static final String GET_ROLE_KILL_MONSTER_BONUS = "2_6";

	public static final String NOTIFY_USER_ENTER = "2_100";
	public static final String NOTIFY_USER_LEAVE = "2_101";
	public static final String NOTIFT_USER_ATTRIBUATE_CHANGE = "2_102";
	public static final String NOTIFY_MONSTER_BE_ATTACK = "2_103";
	public static final String NOTIFY_ROLE_MONSTER_BONUS_FALL = "2_104";
	public static final String NOTIFY_MONSTER_RESURRECTION = "2_105";
	public static final String NOTIFY_MONSTER_ATTRIBUATE_CHANGE = "2_106";
	public static final String NOTIFY_USER_BUFF_ADD = "2_107";
	public static final String NOTIFY_MONSTER_BUFF_ADD = "2_108";
	public static final String NOTIFY_USER_COPY_BEYOND_TIME = "2_109";
	public static final String NOTIFY_USER_COPY_FINISH = "2_110";

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user, CMDdomain cmdDomain) {
		int sceneTypeId = cmdDomain.getIntParam("sti");
		int sceneId = cmdDomain.getIntParam("si");

		ReplyDomain replyDomain = service.userJoinScene(user, sceneTypeId, sceneId);
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
	
	@CmdService(cmd = ATTACK_ROLE)
	public void attackRole(User user, CMDdomain cmdDomain) {
		int skillId = cmdDomain.getIntParam("ski");
		int otherUserId = cmdDomain.getIntParam("uid");
		
		ReplyDomain replyDomain = service.attackOtherRole(user, skillId, otherUserId);

		replyDomain.setStringDomain("cmd", ATTACK_ROLE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_ROLE_KILL_MONSTER_BONUS_INFO)
	public void getRoleKillMonsterBonusInfo(User user, CMDdomain cmdDomain) {
		ReplyDomain replyDomain = service.getRoleKillMonsterBonusInfo(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_ROLE_KILL_MONSTER_BONUS)
	public void getRoleKillMonster(User user, CMDdomain cmdDomain) {
		int bonusId = cmdDomain.getIntParam("bid");

		ReplyDomain replyDomain = service.getRoleKillMonsterBonus(user, bonusId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		replyDomain.setIntDomain("uid", user.getUserId());
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}