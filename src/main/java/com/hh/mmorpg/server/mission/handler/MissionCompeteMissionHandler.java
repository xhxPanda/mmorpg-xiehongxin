package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.MissionCompeteData;

public class MissionCompeteMissionHandler extends AbstractMissionHandler<MissionCompeteData> {

	@Override
	public void dealMission(MissionCompeteData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		int missionId = eventData.getMission();

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

			if (roleMission.isMissionContainKey(String.valueOf(missionId))) {
				roleMission.updateMissionProcess(String.valueOf(missionId), 1);
				if (roleMission.isMissionCompete()) {
					dealFinishMission(role, roleMission);
				}
			}

		}
	}

}
