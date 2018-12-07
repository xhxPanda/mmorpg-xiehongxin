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
	private static final String ATTACK_MONSTER = "attackMonster"; // 攻击怪物
	private static final String ATTACK_ROLE = "attackRole"; // 攻击角色
	private static final String GET_ROLE_KILL_MONSTER_BONUS_INFO = "getRoleKillMonsterBonusInfo";
	private static final String GET_ROLE_KILL_MONSTER_BONUS = "getRoleKillMonsterBonus";
	private static final String JOIN_COPY_SCENE = "joinCopyScene"; // 进入副本

	private static final String TALK_TO_NPC = "talkToNpc"; // 与npc交谈

	public static final String NOTIFY_USER_ENTER = "用户进入场景";
	public static final String NOTIFY_USER_LEAVE = "用户离开场景";
	public static final String NOTIFT_USER_ATTRIBUATE_CHANGE = "角色属性变化";
	public static final String NOTIFY_MONSTER_BE_ATTACK = "场景中怪物被攻击";
	public static final String NOTIFY_ROLE_MONSTER_BONUS_FALL = "怪物掉落生成";
	public static final String NOTIFY_MONSTER_RESURRECTION = "怪物复活";
	public static final String NOTIFY_MONSTER_ATTRIBUATE_CHANGE = "怪物属性变化";
	public static final String NOTIFY_USER_BUFF_ADD = "角色挂上buff";
	public static final String NOTIFY_MONSTER_BUFF_ADD = "怪物挂上buff";
	public static final String NOTIFY_USER_COPY_BEYOND_TIME = "副本超时";
	public static final String NOTIFY_USER_COPY_FINISH = "完成副本";
	public static final String NOTIFY_MONSTER_ENTRE = "刷新怪物";

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user, CMDdomain cmdDomain) {
		int sceneTypeId = cmdDomain.getIntParam(2);
		int sceneId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.userJoinScene(user, sceneTypeId, sceneId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_SCENE_USER)
	public void getSceneUser(User user, CMDdomain cmdDomain) {
		ReplyDomain replyDomain = service.getSeceneUser(user);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = ATTACK_MONSTER)
	public void attackMonster(User user, CMDdomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(2);
		int monsterId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.attackMonster(user, skillId, monsterId);

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

	@CmdService(cmd = TALK_TO_NPC)
	public void takeToNpc(User user, CMDdomain cmdDomain) {
		int npcId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.taklToNpc(user, npcId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = JOIN_COPY_SCENE)
	public void joinCopyScene(User user, CMDdomain cmdDomain) {
		int sceneTypeId = cmdDomain.getIntParam(2);
		int sceneId = cmdDomain.getIntParam(3);
		ReplyDomain replyDomain = service.joinCopyScene(user, sceneTypeId, sceneId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}