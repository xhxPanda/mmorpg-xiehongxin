package com.hh.mmorpg.server.mission;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.jdbc.ConnectionDeal;

public class MissionDao {

	public static final MissionDao INSTANCE = new MissionDao();

	public static final String INSTERT_MISSION = "REPLACE INTO `rolemission0` (`roleId`, `missionId`, `name`, `status`, `process`, `dec`, `type`) VALUES (?, ?, ?, ?, ?, ?, ?);";
	public static final String SELECT_MISSION = "SELECT * FROM `rolemission0` WHERE `roleId` = ?";

	public void accectMission(RoleMission mission) {
		ConnectionDeal.INSTANCE.excuteObject(INSTERT_MISSION,
				new Object[] { mission.getRoleId(), mission.getMissionId(), mission.getName(), mission.getStatus(),
						mission.getDbAttr(), mission.getDec(), mission.getType() });
	}

	public void insertMission(Collection<RoleMission> list) {

		for (RoleMission mission : list) {
			ConnectionDeal.INSTANCE.excuteObject(INSTERT_MISSION,
					new Object[] { mission.getRoleId(), mission.getMissionId(), mission.getName(), mission.getStatus(),
							mission.getDbAttr(), mission.getDec(), mission.getType() });
		}
	}

	public List<RoleMission> getRoleMissions(int roleId) {
		List<RoleMission> list = null;
		try {
			list = (List<RoleMission>) ConnectionDeal.INSTANCE.excuteObjectList(SELECT_MISSION,
					new Object[] { roleId }, RoleMission.BUILDER);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
