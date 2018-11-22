package com.hh.mmorpg.server.mission.handler;

import java.util.Map;

import com.hh.mmorpg.domain.MissionType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.event.data.GuildJoinData;

/**
 * 处理有关加入公会的任务
 * 
 * @author xhx
 *
 */
public class GuildJoinMissionHandler extends AbstractMissionHandler<GuildJoinData> {

	private static final String MISSION_ATT_KEY_Guild = "g";

	/**
	 * 获取用户升级信息，处理有关升级的任务
	 */
	@Override
	public void dealMission(GuildJoinData eventData) {
		// TODO Auto-generated method stub
		Role role = eventData.getRole();

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

			roleMission.updateMissionProcess(MISSION_ATT_KEY_Guild, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
