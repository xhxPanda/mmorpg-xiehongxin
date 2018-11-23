package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.JoinTeamData;

public class JoinTeamMission extends AbstractMissionHandler<JoinTeamData>{

	private static final String MISSION_ATT_KEY_TEAM = "t";
	
	@Override
	public void dealMission(JoinTeamData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		Map<Integer, RoleMission> missionMap = role.getRoleMissionCache().get(MissionType.TEAM_MISSION);
		if (missionMap == null || missionMap.size() == 0) {
			return;
		}

		for (RoleMission roleMission : missionMap.values()) {
			if (roleMission.getStatus() == 1) {
				continue;
			}
			if (roleMission.isMissionCompete()) {
				continue;
			}
			roleMission.updateMissionProcess(MISSION_ATT_KEY_TEAM, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
