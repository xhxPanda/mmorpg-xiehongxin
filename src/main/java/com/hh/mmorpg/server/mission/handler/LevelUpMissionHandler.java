package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.UpdateLevelData;

/**
 * 处理有关用户升级的任务
 * 
 * @author xhx
 *
 */
public class LevelUpMissionHandler extends AbstractMissionHandler<UpdateLevelData> {

	private static final String MISSION_ATT_KEY_LEVEL = "l";

	/**
	 * 获取用户升级信息，处理有关升级的任务
	 */
	@Override
	public void dealMission(UpdateLevelData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		int upLevel = eventData.getNewLevel() - eventData.getOldLevel();

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
			roleMission.updateMissionProcess(MISSION_ATT_KEY_LEVEL, upLevel);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
