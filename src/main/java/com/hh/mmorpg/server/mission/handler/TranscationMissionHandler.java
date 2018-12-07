package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.TransactionData;

public class TranscationMissionHandler extends AbstractMissionHandler<TransactionData> {

	private static final String MISSION_ATT_KEY_TRANSCATION = "t";

	@Override
	public void dealMission(TransactionData eventData, List<RoleMission> missions) {
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

			roleMission.updateMissionProcess(MISSION_ATT_KEY_TRANSCATION, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
