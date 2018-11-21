package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
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
	public void dealMission(NpcTalkData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

		int npcId = eventData.getNpcId();

		Map<Integer, RoleMission> missionMap = role.getRoleMissionCache().get(MissionType.TLAK_NPC);
		if (missionMap == null || missionMap.size() == 0) {
			return;
		}

		for (RoleMission roleMission : missionMap.values()) {
			if (roleMission.getStatus() == 1) {
				continue;
			}
			if (roleMission.isMissionConpete()) {
				continue;
			}
			if (npcId != Integer.parseInt(roleMission.getDec())) {
				continue;
			}

			roleMission.updateMissionProcess(MISSION_ATT_KEY_LEVEL, 1);
			if (roleMission.isMissionConpete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
