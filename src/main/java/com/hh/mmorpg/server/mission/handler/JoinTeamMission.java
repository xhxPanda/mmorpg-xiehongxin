package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.JoinTeamData;

public class JoinTeamMission extends AbstractMissionHandler<JoinTeamData> {

	private static final String MISSION_ATT_KEY_TEAM = "t";

	@Override
	public void dealMission(JoinTeamData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		if (missions.size() == 0) {
			return;
		}

		for (RoleMission roleMission : missions) {
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
