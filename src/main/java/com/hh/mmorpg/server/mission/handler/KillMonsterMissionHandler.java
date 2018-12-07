package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.MonsterDeadData;

public class KillMonsterMissionHandler extends AbstractMissionHandler<MonsterDeadData> {

	@Override
	public void dealMission(MonsterDeadData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub

		Role role = eventData.getKillRole();

		Monster monster = eventData.getMonster();

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

			if (roleMission.isMissionContainKey(String.valueOf(monster.getId()))) {
				roleMission.updateMissionProcess(String.valueOf(monster.getId()), 1);
				notifyMissionStatusChange(role, roleMission);
			}

			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
