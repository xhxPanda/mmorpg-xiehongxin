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

	private static final String JOIN_SCENE = "joinScene";
	private static final String GET_SCENE_USER = "getSceneUser";
	private static final String ATTACK_MONSTER = "attackMonster";
	private static final String ATTACK_ROLE = "attackRole";
	private static final String GET_ROLE_KILL_MONSTER_BONUS_INFO = "getRoleKillMonsterBonusInfo";
	private static final String GET_ROLE_KILL_MONSTER_BONUS = "getRoleKillMonsterBonus";

	public static final String NOTIFY_USER_ENTER = "notifyUserEntry";
	public static final String NOTIFY_USER_LEAVE = "notifyUserLeace";
	public static final String NOTIFT_USER_ATTRIBUATE_CHANGE = "notifyUserAttributeChange";
	public static final String NOTIFY_MONSTER_BE_ATTACK = "notifyMonsterBeAttack";
	public static final String NOTIFY_ROLE_MONSTER_BONUS_FALL = "notifyRoleMonsterBonusFall";
	public static final String NOTIFY_MONSTER_RESURRECTION = "notifyMonsterResurrection";
	public static final String NOTIFY_MONSTER_ATTRIBUATE_CHANGE = "notifyMonsterAttrubuateChange";
	public static final String NOTIFY_USER_BUFF_ADD = "notifyUserBuffAdd";
	public static final String NOTIFY_MONSTER_BUFF_ADD = "notifyMonsterBuffAdd";
	public static final String NOTIFY_USER_COPY_BEYOND_TIME = "notifyUserCopyBeyondTime";
	public static final String NOTIFY_USER_COPY_FINISH = "notifyUserCopyFinish";
	public static final String NOTIFY_MONSTER_ENTRE = "notifyMonsterEntre";

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user,  CMDdomain cmdDomain) {
		int sceneTypeId = cmdDomain.getIntParam(2);
		int sceneId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.userJoinScene(user, sceneTypeId, sceneId);
		replyDomain.setStringDomain("cmd", JOIN_SCENE);
		replyDomain.setStringDomain("cmdDir", "进入场景");
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
		int skillId = cmdDomain.getIntParam(2);
		int monsterId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.attackMonster(user, skillId, monsterId);

		replyDomain.setStringDomain("cmd", ATTACK_MONSTER);
		replyDomain.setStringDomain("cmdDir", "攻击怪物");
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = ATTACK_ROLE)
	public void attackRole(User user, CMDdomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(2);
		int otherUserId = cmdDomain.getIntParam(3);
		
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
		int bonusId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.getRoleKillMonsterBonus(user, bonusId);
		
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		replyDomain.setIntDomain("uid", user.getUserId());
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}