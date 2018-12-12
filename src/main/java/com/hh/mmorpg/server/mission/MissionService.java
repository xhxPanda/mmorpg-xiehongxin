package com.hh.mmorpg.server.mission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.hh.mmorpg.event.data.FriendData;
import com.hh.mmorpg.event.data.GainTreasureData;
import com.hh.mmorpg.event.data.GetMaterialData;
import com.hh.mmorpg.event.data.GuildJoinData;
import com.hh.mmorpg.event.data.JoinTeamData;
import com.hh.mmorpg.event.data.MissionCompeteData;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.event.data.NpcTalkData;
import com.hh.mmorpg.event.data.PKData;
import com.hh.mmorpg.event.data.PassCopyData;
import com.hh.mmorpg.event.data.TransactionData;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.event.data.UserEquimentData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.mission.handler.AbstractMissionHandler;
import com.hh.mmorpg.server.mission.handler.AddFriendMissionHandler;
import com.hh.mmorpg.server.mission.handler.CopyMissionHandler;
import com.hh.mmorpg.server.mission.handler.EquimentMissionHandler;
import com.hh.mmorpg.server.mission.handler.GuildJoinMissionHandler;
import com.hh.mmorpg.server.mission.handler.JoinTeamMission;
import com.hh.mmorpg.server.mission.handler.KillMonsterMissionHandler;
import com.hh.mmorpg.server.mission.handler.LevelUpMissionHandler;
import com.hh.mmorpg.server.mission.handler.MaterialMissionHandler;
import com.hh.mmorpg.server.mission.handler.MissionCompeteMissionHandler;
import com.hh.mmorpg.server.mission.handler.NpcTalkMissionHandler;
import com.hh.mmorpg.server.mission.handler.PKMissionHandler;
import com.hh.mmorpg.server.mission.handler.TranscationMissionHandler;
import com.hh.mmorpg.server.mission.handler.TreasureMissionHandler;
import com.hh.mmorpg.server.role.RoleService;

public class MissionService {

	public static final MissionService INSTANCE = new MissionService();

	private Map<Integer, MissionDomain> missionDomainMap;

	@SuppressWarnings("rawtypes")
	private Map<Integer, AbstractMissionHandler> handlerMap;

	private MissionService() {
		missionDomainMap = MissionXmlResolution.INSTANCE.resolution();

		handlerMap = new HashMap<>();
		handlerMap.put(MissionType.LEVEL_MISSION, new LevelUpMissionHandler());
		handlerMap.put(MissionType.TLAK_NPC, new NpcTalkMissionHandler());
		handlerMap.put(MissionType.MATERIAL_MISSION, new MaterialMissionHandler());
		handlerMap.put(MissionType.EQUIMENT_MISSION, new EquimentMissionHandler());
		handlerMap.put(MissionType.GUILD_MISSION, new GuildJoinMissionHandler());
		handlerMap.put(MissionType.KILL_MONSTER, new KillMonsterMissionHandler());
		handlerMap.put(MissionType.MISSION_CONPETE, new MissionCompeteMissionHandler());
		handlerMap.put(MissionType.ADD_FIREND, new AddFriendMissionHandler());
		handlerMap.put(MissionType.TEAM_MISSION, new JoinTeamMission());
		handlerMap.put(MissionType.COPY, new CopyMissionHandler());
		handlerMap.put(MissionType.TREASURE, new TreasureMissionHandler());
		handlerMap.put(MissionType.TRANSCATION, new TranscationMissionHandler());
		handlerMap.put(MissionType.PK, new PKMissionHandler());

		// 注册监听
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
		if (missionDomain == null) {
			return ReplyDomain.FAILE;
		}

		if (missionDomain.getNeedLevel() > role.getLevel()) {
			return ReplyDomain.LEVEL_NOT_ENOUGH;
		}

		RoleMission mission = role.getRoleMissionMap().get(missionId);
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
	public ReplyDomain missionCompete(User user, int missionId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		RoleMission roleMission = role.getRoleMissionMap().get(missionId);
		if (roleMission == null) {
			return ReplyDomain.MISSION_NOT_EXIST;
		}

		// 设置任务状态为已领取奖品
		roleMission.setStatus(2);

		MissionDomain missionDomain = missionDomainMap.get(missionId);
		MaterialService.INSTANCE.gainMasteral(user, role, missionDomain.getBonus());
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 放弃任务
	 * 
	 * @param user
	 * @param missionId
	 * @return
	 */
	public ReplyDomain giveUpMission(User user, int missionId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		RoleMission roleMission = role.getRoleMissionMap().get(missionId);
		if (roleMission == null) {
			return ReplyDomain.MISSION_NOT_EXIST;
		}
		role.getRoleMissionMap().remove(missionId);
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 展示已接受的任务
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showMissionAccept(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		Map<Integer, RoleMission> roleMission = role.getRoleMissionMap();

		// 去除已领奖的任务
		List<RoleMission> roleMissions = new ArrayList<>();
		for (RoleMission mission : roleMission.values()) {
			if (mission.getStatus() == 2) {
				continue;
			}
			roleMissions.add(mission);
		}

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("已接受任务", roleMissions);

		return replyDomain;
	}

	/**
	 * 展示可接受的任务
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showMissionCanAccept(User user) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		ReplyDomain replyDomain = new ReplyDomain();

		List<MissionDomain> list = new ArrayList<MissionDomain>();
		for (MissionDomain missionDomain : missionDomainMap.values()) {
			if (missionDomain.getNeedLevel() > role.getLevel()
					|| role.getRoleMissionMap().containsKey(missionDomain.getId())) {
				continue;
			}

			list.add(missionDomain);
		}

		replyDomain.setListDomain("可接任务列表", list);
		return replyDomain;
	}

	/**
	 * 获取任务定义类
	 * 
	 * @param id
	 * @return
	 */
	public MissionDomain getMissionDomain(int id) {
		return missionDomainMap.get(id);
	}

	// 处理用户升级的任务
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.LEVEL_UP)
	public void handleUserLevelUp(EventDealData<UpdateLevelData> data) {
		UpdateLevelData levelData = data.getData();
		handlerMap.get(MissionType.LEVEL_MISSION).dealMission(levelData,
				getRoleMissionByType(levelData.getRole(), MissionType.LEVEL_MISSION));
	}

	// 处理用户与npc的任务
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.TALK_TO_NPC)
	public void handleUsertalkToNpc(EventDealData<NpcTalkData> data) {
		NpcTalkData npcTalkData = data.getData();
		handlerMap.get(MissionType.TLAK_NPC).dealMission(npcTalkData,
				getRoleMissionByType(npcTalkData.getRole(), MissionType.TLAK_NPC));
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
		handlerMap.get(MissionType.MATERIAL_MISSION).dealMission(getMaterialData,
				getRoleMissionByType(getMaterialData.getRole(), MissionType.MATERIAL_MISSION));
	}

	// 处理用户穿装备的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.WEAR_QUEIMENT)
	public void handleUserWearEquiment(EventDealData<UserEquimentData> data) {
		UserEquimentData equimentLevelData = data.getData();
		handlerMap.get(MissionType.EQUIMENT_MISSION).dealMission(equimentLevelData,
				getRoleMissionByType(equimentLevelData.getRole(), MissionType.EQUIMENT_MISSION));
	}

	// 处理用户加入公会的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.JOIN_GUILD)
	public void handleUserJoinGuild(EventDealData<GuildJoinData> data) {
		GuildJoinData guildJoinData = data.getData();
		handlerMap.get(MissionType.GUILD_MISSION).dealMission(guildJoinData,
				getRoleMissionByType(guildJoinData.getRole(), MissionType.GUILD_MISSION));
	}

	// 处理用户加入队伍的事件
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.JOIN_TEAM)
	public void handleUserJoinTeam(EventDealData<JoinTeamData> data) {
		JoinTeamData joinTeamData = data.getData();
		handlerMap.get(MissionType.TEAM_MISSION).dealMission(joinTeamData,
				getRoleMissionByType(joinTeamData.getRole(), MissionType.TEAM_MISSION));
	}

	/**
	 * 完成任务
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.MISSION_COMPETE)
	public void handleUserMissionCompete(EventDealData<MissionCompeteData> data) {
		MissionCompeteData competeData = data.getData();
		handlerMap.get(MissionType.MISSION_CONPETE).dealMission(competeData,
				getRoleMissionByType(competeData.getRole(), MissionType.MISSION_CONPETE));
	}

	/**
	 * 打怪
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.MONSTER_DEAD)
	public void handleMonsterDead(EventDealData<MonsterDeadData> data) {
		MonsterDeadData monsterDeadData = data.getData();
		handlerMap.get(MissionType.KILL_MONSTER).dealMission(monsterDeadData,
				getRoleMissionByType(monsterDeadData.getKillRole(), MissionType.KILL_MONSTER));
	}

	/**
	 * 加为好友
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.BECOME_FRIEND)
	public void handleAddFriend(EventDealData<FriendData> data) {
		FriendData friendData = data.getData();
		handlerMap.get(MissionType.ADD_FIREND).dealMission(friendData,
				getRoleMissionByType(friendData.getRole(), MissionType.ADD_FIREND));
	}

	/**
	 * 通关副本
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.PASS_COPY)
	public void handlePassCopy(EventDealData<PassCopyData> data) {
		PassCopyData passCopyData = data.getData();
		handlerMap.get(MissionType.COPY).dealMission(passCopyData,
				getRoleMissionByType(passCopyData.getRole(), MissionType.COPY));
	}

	/**
	 * 获得财富
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.TREASURE)
	public void handleGainTreasure(EventDealData<GainTreasureData> data) {
		GainTreasureData passCopyData = data.getData();
		handlerMap.get(MissionType.TREASURE).dealMission(passCopyData,
				getRoleMissionByType(passCopyData.getRole(), MissionType.TREASURE));
	}

	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.PK)
	public void handleUserPK(EventDealData<PKData> data) {
		PKData pkData = data.getData();
		Role winRole = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(pkData.getWinRoleId()),
				pkData.getWinRoleId());
		handlerMap.get(MissionType.PK).dealMission(pkData, getRoleMissionByType(winRole, MissionType.PK));
	}

	@SuppressWarnings("unchecked")
	@Event(eventType = EventType.TRANSACTION)
	public void handleUserTransaction(EventDealData<TransactionData> data) {
		TransactionData transactionData = data.getData();

		handlerMap.get(MissionType.TRANSCATION).dealMission(transactionData,
				getRoleMissionByType(transactionData.getRole(), MissionType.TRANSCATION));
	}

	private List<RoleMission> getRoleMissionByType(Role role, int type) {
		List<RoleMission> missionList = new ArrayList<>();

		Map<Integer, RoleMission> map = role.getRoleMissionMap();
		for (RoleMission roleMission : map.values()) {
			if (roleMission != null && roleMission.getType() == type) {
				missionList.add(roleMission);
			}
		}

		return missionList;
	}

}
