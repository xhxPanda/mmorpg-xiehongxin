package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.FriendData;

public class FriendMissionHandler extends AbstractMissionHandler<FriendData> {

	private static final String MISSION_ATT_KEY_FRIEND = "f";

	@Override
	public void dealMission(FriendData eventData, List<RoleMission> missions) {
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
			roleMission.updateMissionProcess(MISSION_ATT_KEY_FRIEND, 1);
			notifyMissionStatusChange(role, roleMission);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
