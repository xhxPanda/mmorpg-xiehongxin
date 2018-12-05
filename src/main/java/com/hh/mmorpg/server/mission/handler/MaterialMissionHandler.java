package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.GetMaterialData;

/**
 * 处理物品获得的任务
 * @author xhx
 *
 */
public class MaterialMissionHandler extends AbstractMissionHandler<GetMaterialData> {

	@Override
	public void dealMission(GetMaterialData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		BagMaterial bagMaterial = eventData.getBagMaterial();
		int num = eventData.getNum();
		if(role.getRoleMissionCache() == null) {
			return;
		}
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

			String key = String.valueOf(bagMaterial.getId());
			if (roleMission.isMissionContainKey(key)) {
				roleMission.updateMissionProcess(key, num);
				if (roleMission.isMissionCompete()) {
					dealFinishMission(role, roleMission);
				}
			}

		}
	}

}
