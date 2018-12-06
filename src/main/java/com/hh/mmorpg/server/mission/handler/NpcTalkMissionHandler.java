package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.NpcTalkData;

/**
 * 处理有关用户升级的任务
 * 
 * @author xhx
 *
 */
public class NpcTalkMissionHandler extends AbstractMissionHandler<NpcTalkData> {

	private static final String MISSION_ATT_KEY_LEVEL = "l";

	/**
	 * 获取用户升级信息，处理有关升级的任务
	 */
	@Override
	public void dealMission(NpcTalkData eventData, List<RoleMission> missions) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		int npcId = eventData.getNpcId();

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
			if (npcId != Integer.parseInt(roleMission.getDec())) {
				continue;
			}

			roleMission.updateMissionProcess(MISSION_ATT_KEY_LEVEL, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
