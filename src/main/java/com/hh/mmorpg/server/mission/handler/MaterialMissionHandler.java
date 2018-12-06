package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.GetMaterialData;

/**
 * 处理物品获得的任务
 * 
 * @author xhx
 *
 */
public class MaterialMissionHandler extends AbstractMissionHandler<GetMaterialData> {

	@Override
	public void dealMission(GetMaterialData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		BagMaterial bagMaterial = eventData.getBagMaterial();
		int num = eventData.getNum();

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
