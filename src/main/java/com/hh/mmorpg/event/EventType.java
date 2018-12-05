package com.hh.mmorpg.event;

/**
 * 事件类型
 * 
 * @author xhx
 *
 */
public class EventType {

	public static final int USER_LOST = 1; // 用户下线
	public static final int MONSTER_DEAD = 2; // 击杀怪物
	public static final int ROLE_CHANGE = 3; // 替换角色
	public static final int LEVEL_UP = 4; // 升级
	public static final int MISSION_COMPETE = 5; // 完成任务
	public static final int TALK_TO_NPC = 6; // 与npc交谈
	public static final int GET_MATERIAL = 7; // 获得物品
	public static final int WEAR_QUEIMENT = 8; // 穿上装备
	public static final int JOIN_GUILD = 9; // 加入公会
	public static final int JOIN_TEAM = 10; // 加入队伍
	public static final int BECOME_FRIEND = 11; // 结为好友
	public static final int PK = 12; // pk

}
