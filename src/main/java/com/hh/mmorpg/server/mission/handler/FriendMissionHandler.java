package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.FriendData;

public class FriendMissionHandler extends AbstractMissionHandler<FriendData>{

	private static final String MISSION_ATT_KEY_FRIEND = "f";
	
	@Override
	public void dealMission(FriendData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		Map<Integer, RoleMission> missionMap = role.getRoleMissionCache().get(MissionType.ADD_FIREND);
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

			roleMission.updateMissionProcess(MISSION_ATT_KEY_FRIEND, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
