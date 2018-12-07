package com.hh.mmorpg.server.mission.handler;

import java.util.List;
import java.util.Map;

import com.hh.mmorpg.domain.MissionAttribute;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.data.UserEquimentData;

/**
 * 身上的装备等级
 * 
 * @author xhx
 *
 */
public class EquimentMissionHandler extends AbstractMissionHandler<UserEquimentData> {

	private static final String MISSION_LEVEL = "ml";

	@Override
	public void dealMission(UserEquimentData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub

		if (missions.size() == 0) {
			return;
		}

		Role role = eventData.getRole();
		Map<Integer, UserEquipment> equiments = role.getEquipmentMap();

		int totalSource = 0;
		for (UserEquipment userEquipment : equiments.values()) {
			totalSource += userEquipment.getEquimentSource();
		}

		for (RoleMission roleMission : missions) {
			if (roleMission.getStatus() == 1) {
				continue;
			}
			if (roleMission.isMissionCompete()) {
				continue;
			}

			MissionAttribute missionAttribute = roleMission.getAtt(MISSION_LEVEL);
			if (missionAttribute.getMax() <= totalSource) {
				missionAttribute
						.setprocess(missionAttribute.getMax() <= totalSource ? missionAttribute.getMax() : totalSource);
			}
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}

	}

}
