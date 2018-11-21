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
import com.hh.mmorpg.event.data.NpcTalkData;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.mission.handler.AbstractMissionHandler;
import com.hh.mmorpg.server.mission.handler.LevelUpMissionHandler;
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
	@Event(eventType = EventType.LEVEL_UP)
	public void handleUserLevelUp(EventDealData<UpdateLevelData> data) {
		UpdateLevelData userLostData = data.getData();

		handlerMap.get(MissionType.LEVEL_MISSION).dealMission(userLostData);
	}

	// 处理用户升级的任务
	@Event(eventType = EventType.TALK_TO_NPC)
	public void handleUsertalkToNpc(EventDealData<NpcTalkData> data) {
		NpcTalkData userLostData = data.getData();

		handlerMap.get(MissionType.TLAK_NPC).dealMission(userLostData);
	}
}
