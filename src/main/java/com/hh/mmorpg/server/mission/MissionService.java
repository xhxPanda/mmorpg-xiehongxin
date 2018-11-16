package com.hh.mmorpg.server.mission;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.MissionDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleService;

public class MissionService {

	public static final MissionService INSTANCE = new MissionService();

	private Map<Integer, MissionDomain> missionDomainMap = new HashMap<>();
	private Map<Integer, Map<Integer, RoleMission>> roleMissionCache;

	private MissionService() {
		missionDomainMap = new HashMap<>();
		roleMissionCache = new HashMap<>();
	}

	public ReplyDomain accpetMission(User user, int missionId) {

//		MissionDomain missionDomain = missionDomainMap.get(missionId);

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain missionCompete(User user, int missionId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		RoleMission roleMission = roleMissionCache.get(role.getRoleId()).get(missionId);
		if (roleMission == null) {
			return ReplyDomain.MISSION_NOT_EXIST;
		}

		MissionDomain missionDomain = missionDomainMap.get(missionId);
		MaterialService.INSTANCE.gainMasteral(user, role, missionDomain.getBonus());
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain giveUpMission(User user, int missionId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		RoleMission roleMission = roleMissionCache.get(role.getRoleId()).get(missionId);
		if (roleMission == null) {
			return ReplyDomain.MISSION_NOT_EXIST;
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain showMissionAccept(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		
		Map<Integer, RoleMission> roleMission = roleMissionCache.get(role.getId());
		
		ReplyDomain replyDomain = new ReplyDomain();
		return replyDomain;
	}

	public ReplyDomain showMissionCanAccept(User user) {
		// TODO Auto-generated method stub
		ReplyDomain replyDomain = new ReplyDomain();
		return replyDomain;
	}
}
