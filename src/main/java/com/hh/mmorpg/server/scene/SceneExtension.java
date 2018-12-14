package com.hh.mmorpg.server.scene;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.NotifiesWarehouse;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 2)
public class SceneExtension {

	private static SceneService service = SceneService.INSTANCE;

	private static final String JOIN_SCENE = "joinScene"; // 进入场景
	private static final String GET_SCENE_USER = "getSceneUser"; // 获取场景信息
	private static final String ATTACK_MONSTER = "attackMonster"; // 攻击怪物
	private static final String ATTACK_ROLE = "attackRole"; // 攻击角色
	private static final String GET_ROLE_KILL_MONSTER_BONUS_INFO = "getRoleKillMonsterBonusInfo"; // 获取场景中可以捡取的物品
	private static final String GET_ROLE_KILL_MONSTER_BONUS = "getRoleKillMonsterBonus"; // 捡取掉落物品
	private static final String GET_COPY_INFO = "getCopyInfo"; // 获取副本信息
	private static final String JOIN_COPY_SCENE = "joinCopyScene"; // 进入副本
	private static final String TALK_TO_NPC = "talkToNpc"; // 与npc交谈

	public static final String NOTIFY_USER_ENTER = NotifiesWarehouse.INSTANCE.getNotifyContent("NOTIFY_USER_ENTER");
	public static final String NOTIFY_USER_LEAVE = NotifiesWarehouse.INSTANCE.getNotifyContent("NOTIFY_USER_LEAVE");
	public static final String NOTIFT_USER_ATTRIBUATE_CHANGE = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFT_USER_ATTRIBUATE_CHANGE");
	public static final String NOTIFY_MONSTER_BE_ATTACK = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_MONSTER_BE_ATTACK");
	public static final String NOTIFY_ROLE_MONSTER_BONUS_FALL = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_ROLE_MONSTER_BONUS_FALL");
	public static final String NOTIFY_MONSTER_RESURRECTION = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_MONSTER_RESURRECTION");
	public static final String NOTIFY_MONSTER_ATTRIBUATE_CHANGE = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_MONSTER_ATTRIBUATE_CHANGE");
	public static final String NOTIFY_USER_BUFF_ADD = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_USER_BUFF_ADD");
	public static final String NOTIFY_MONSTER_BUFF_ADD = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_MONSTER_BUFF_ADD");
	public static final String NOTIFY_USER_COPY_BEYOND_TIME = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_USER_COPY_BEYOND_TIME");
	public static final String NOTIFY_USER_COPY_FINISH = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_USER_COPY_FINISH");
	public static final String NOTIFY_MONSTER_ENTRE = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_MONSTER_ENTRE");

	@CmdService(cmd = JOIN_SCENE)
	public void joinScene(User user, CmdDomain cmdDomain) {
		int sceneId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.userJoinScene(user, sceneId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_COPY_INFO)
	public void getCopyInfo(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.getCopyInfo(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_SCENE_USER)
	public void getSceneUser(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.getSeceneUser(user);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = ATTACK_MONSTER)
	public void attackMonster(User user, CmdDomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(2);
		int monsterId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.attackMonster(user, skillId, monsterId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = ATTACK_ROLE)
	public void attackRole(User user, CmdDomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(2);
		int otherUserId = cmdDomain.getIntParam(3);

		ReplyDomain replyDomain = service.attackOtherRole(user, skillId, otherUserId);

		replyDomain.setStringDomain("cmd", ATTACK_ROLE);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_ROLE_KILL_MONSTER_BONUS_INFO)
	public void getRoleKillMonsterBonusInfo(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.getRoleKillMonsterBonusInfo(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_ROLE_KILL_MONSTER_BONUS)
	public void getRoleKillMonster(User user, CmdDomain cmdDomain) {
		int bonusId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.getRoleKillMonsterBonus(user, bonusId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = TALK_TO_NPC)
	public void takeToNpc(User user, CmdDomain cmdDomain) {
		int npcId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.taklToNpc(user, npcId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = JOIN_COPY_SCENE)
	public void joinCopyScene(User user, CmdDomain cmdDomain) {
		int sceneTypeId = cmdDomain.getIntParam(2);
		ReplyDomain replyDomain = service.joinCopyScene(user, sceneTypeId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}