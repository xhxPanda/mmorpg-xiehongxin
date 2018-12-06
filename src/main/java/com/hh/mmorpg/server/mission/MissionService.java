package com.hh.mmorpg.server.mission;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.MissionDomain;
import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.EquimentLevelData;
import com.hh.mmorpg.event.data.GetMaterialData;
import com.hh.mmorpg.event.data.GuildJoinData;
import com.hh.mmorpg.event.data.JoinTeamData;
import com.hh.mmorpg.event.data.NpcTalkData;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.mission.handler.AbstractMissionHandler;
import com.hh.mmorpg.server.mission.handler.EquimentMissionHandler;
import com.hh.mmorpg.server.mission.handler.GuildJoinMissionHandler;
import com.hh.mmorpg.server.mission.handler.LevelUpMissionHandler;
import com.hh.mmorpg.server.mission.handler.MaterialMissionHandler;
import com.hh.mmorpg.server.mission.handler.NpcTalkMissionHandler;
import com.hh.mmorpg.server.role.RoleService;

public class MissionService {

	public static final MissionService INSTANCE = new MissionService();

	private Map<Integer, MissionDomain> missionDomainMap = new HashMap<>();

	@SuppressWarnings("rawtypes")
	private Map<Integer, AbstractMissionHandler> handlerMap;

	private MissionService() {
		missionDomainMap = new HashMap<>();

		handlerMap = new HashMap<>();
		handlerMap.put(MissionType.LEVEL_MISSION, new LevelUpMissionHandler());
		handlerMap.put(MissionType.TLAK_NPC, new NpcTalkMissionHandler());
		handlerMap.put(MissionType.MATERIAL_MISSION, new MaterialMissionHandler());
		handlerMap.put(MissionType.EQUIMENT_MISSION, new EquimentMissionHandler());
		handlerMap.put(MissionType.GUILD_MISSION, new GuildJoinMissionHandler());

		EventHandlerManager.INSATNCE.register(this);
	}

	/**
	 * 接受任务
	 * 
	 * @param user
	 * @param missionId
	 * @return
	 */
	public ReplyDomain accpetMission(User user, int missionId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		MissionDomain missionDomain = missionDomainMap.get(missionId);

		if (missionDomain.getNeedLevel() > role.getLevel()) {
			return ReplyDomain.LEVEL_NOT_ENOUGH;
		}

		RoleMission mission = role.getMission(missionDomain.getType(), missionDomain.getId());
		if (mission != null) {
			return ReplyDomain.HAD_HAS_MISSION;
		}

		RoleMission roleMission = new RoleMission(missionId, role.getId(), missionDomain.getName(),
				missionDomain.getDec(), missionDomain.getType(), -1, missionDomain.getCompeteAttribute());
		role.reciveMission(roleMission);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 完成任务，去领奖
	 * 
	 * @param user
	 * @param missionId
	 * @return
	 */
//	public ReplyDomain missionCompete(User user, int missionId) {
//		int userId = user.getUserId();
//		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
//
//		RoleMission roleMission = roleMissionCache.get(role.getRoleId()).get(missionId);
//		if (roleMission == null) {
//			return ReplyDomain.MISSION_NOT_EXIST;
//		}
//
//		MissionDomain missionDomain = missionDomainMap.get(missionId);
//		MaterialService.INSTANCE.gainMasteral(user, role, missionDomain.getBonus());
//		return ReplyDomain.SUCCESS;
//	}

//	public ReplyDomain giveUpMission(User user, int missionId) {
//		int userId = user.getUserId();
//		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
//
//		RoleMission roleMission = roleMissionCache.get(role.getRoleId()).get(missionId);
//		if (roleMission == null) {
//			return ReplyDomain.MISSION_NOT_EXIST;
//		}
//
//		return ReplyDomain.SUCCESS;
//	}
//
//	public ReplyDomain showMissionAccept(User user) {
//		// TODO Auto-generated method stub
//		int userId = user.getUserId();
//		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
//
//		Map<Integer, RoleMission> roleMission = roleMissionCache.get(role.getId());
//
//		ReplyDomain replyDomain = new ReplyDomain();
//		return replyDomain;
//	}

	public ReplyDomain showMissionCanAccept(User user) {
		// TODO Auto-generated method stub
		ReplyDomain replyDomain = new ReplyDomain();
		return replyDomain;
	}

//	public Map<Integer, RoleMission> getRoleMissions(int roleId)｛
//		
//	｝

	// 处理用户升级的任务
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.LEVEL_UP)
	public void handleUserLevelUp(EventDealData<UpdateLevelData> data) {
		UpdateLevelData levelData = data.getData();
		handlerMap.get(MissionType.LEVEL_MISSION).dealMission(levelData);
	}

	// 处理用户升级的任务
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.TALK_TO_NPC)
	public void handleUsertalkToNpc(EventDealData<NpcTalkData> data) {
		NpcTalkData npcTalkData = data.getData();
		handlerMap.get(MissionType.TLAK_NPC).dealMission(npcTalkData);
	}

	/**
	 * 处理用户获得物品任务
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.GET_MATERIAL)
	public void handleUserGetMaterial(EventDealData<GetMaterialData> data) {
		GetMaterialData getMaterialData = data.getData();
		handlerMap.get(MissionType.MATERIAL_MISSION).dealMission(getMaterialData);
	}

	// 处理用户穿装备的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.WEAR_QUEIMENT)
	public void handleUserWearEquiment(EventDealData<EquimentLevelData> data) {
		EquimentLevelData equimentLevelData = data.getData();
		handlerMap.get(MissionType.EQUIMENT_MISSION).dealMission(equimentLevelData);
	}

	// 处理用户加入公会的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.JOIN_GUILD)
	public void handleUserJoinGuild(EventDealData<GuildJoinData> data) {
		GuildJoinData guildJoinData = data.getData();
		handlerMap.get(MissionType.GUILD_MISSION).dealMission(guildJoinData);
	}

	// 处理用户加入队伍的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.JOIN_TEAM)
	public void handleUserJoinTeam(EventDealData<JoinTeamData> data) {
		JoinTeamData joinTeamData = data.getData();
		handlerMap.get(MissionType.TEAM_MISSION).dealMission(joinTeamData);
	}
}
