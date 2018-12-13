package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.GainTreasureData;

public class TreasureMissionHandler extends AbstractMissionHandler<GainTreasureData> {

	@Override
	public void dealMission(GainTreasureData eventData, List<RoleMission> missions) {
		Role role = eventData.getRole();
		if (missions.size() == 0) {
			return;
		}

		int treasureId = eventData.getId();

		for (RoleMission roleMission : missions) {
			if (roleMission.getStatus() == 1) {
				continue;
			}
			if (roleMission.isMissionCompete()) {
				continue;
			}

			roleMission.updateMissionProcess(String.valueOf(treasureId), (int) eventData.getGainTreasure());
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
