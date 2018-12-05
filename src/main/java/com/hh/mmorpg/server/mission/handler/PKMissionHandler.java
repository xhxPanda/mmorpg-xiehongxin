package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.PKData;
import com.hh.mmorpg.server.role.RoleService;

public class PKMissionHandler extends AbstractMissionHandler<PKData> {

	private static final String MISSION_ATT_KEY_PK = "p";

	@Override
	public void dealMission(PKData eventData) {
		// TODO Auto-generated method stub
		int winRoleId = eventData.getWinRoleId();

		Role role = RoleService.INSTANCE.getUserRole(RoleService.INSTANCE.getUserId(winRoleId), winRoleId);

		Map<Integer, RoleMission> missionMap = role.getRoleMissionCache().get(MissionType.TLAK_NPC);
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

			roleMission.updateMissionProcess(MISSION_ATT_KEY_PK, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
