package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.MissionAttribute;
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

	/**
	 * 获取用户升级信息，处理有关与npc交谈的任务
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

			if (roleMission.isMissionContainKey(String.valueOf(npcId))) {
				roleMission.updateMissionProcess(String.valueOf(npcId), 1);
				if (roleMission.isMissionCompete()) {
					dealFinishMission(role, roleMission);
				}
			}

		}
	}

}
