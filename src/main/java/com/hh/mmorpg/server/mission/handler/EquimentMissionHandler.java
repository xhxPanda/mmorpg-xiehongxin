package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.data.EquimentLevelData;

/**
 * 身上的装备等级
 * 
 * @author xhx
 *
 */
public class EquimentMissionHandler extends AbstractMissionHandler<EquimentLevelData> {

	@Override
	public void dealMission(EquimentLevelData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		Map<Integer, UserEquipment> equiments = eventData.getUserEquipmentMap();

		Map<Integer, RoleMission> missionMap = role.getRoleMissionCache().get(MissionType.MATERIAL_MISSION);
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

			for (String s : roleMission.getKeys()) {
				for (UserEquipment userEquipment : equiments.values()) {
					// 如果有一件装备不符合，立刻跳出循环
					if (userEquipment.getEquimentLevel() < Integer.parseInt(s)) {
						break;
					}
				}
				roleMission.updateMissionProcess(s, 1);
				if (roleMission.isMissionCompete()) {
					dealFinishMission(role, roleMission);
					break;
				}
			}

		}
	}

}