package com.hh.mmorpg.server.mission.handler;

import java.util.List;

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

	private static final String MISSION_ATT_KEY_GUILD = "g";

	/**
	 * 获取用户公会信息，处理有关公会的任务
	 */
	@Override
	public void dealMission(GuildJoinData eventData, List<RoleMission> missions) {
		Role role = eventData.getRole();

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

			roleMission.updateMissionProcess(MISSION_ATT_KEY_GUILD, 1);
			if (roleMission.isMissionCompete()) {
				dealFinishMission(role, roleMission);
			}
		}
	}

}
