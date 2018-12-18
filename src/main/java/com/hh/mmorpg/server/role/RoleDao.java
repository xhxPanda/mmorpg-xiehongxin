package com.hh.mmorpg.server.role;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.jdbc.JDBCManager;

public class RoleDao {

	public static final RoleDao INSTANCE = new RoleDao();

	private static final int DB_INDEX = 1000 * 10000;

	private static final String SELECT_USER_ROLE = "SELECT * FROM role%s where userId = ?";
	private static final String SELECT_ROLE = "SELECT * FROM role0 where `id` = ?";
	private static final String INSERT_USER_ROLE = "INSERT INTO `role0` (`userId`, `id`, `occupationId`, `name`, `capacity`, `TeamId`, `exp`, `Level`, `lastJoinScene`, `attribute`, `guildId`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_ROLE_SKILL = "REPLACE INTO `RoleSkill` (`roleId`, `SkillId`, `level`, `LastUsedTime`) values (?, ?, ?, ?)";
	private static final String SELECT_ROLE_SKILL = "SELECT * FROM RoleSkill where roleId = ?";

	private static final String UPDATE_USER_GUILD = "UPDATE role0 SET guildId = ? WHERE id = ? AND UserId = ?";

	private static final String UPDATE_USER_ROLE = "REPLACE INTO `role0` (`userId`, `id`, `occupationId`, `name`, `capacity`, `TeamId`, `exp`, `Level`, `lastJoinScene`, `attribute`, `guildId`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_ROLE_TEAM_STATUS = "UPDATE role0 SET TeamId = ? WHERE roleId = ?";

	public List<Role> selectUserRole(int userId) {
		int dbIndex = userId / DB_INDEX;
		List<Role> list = null;
		try {
			list = (List<Role>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(String.format(SELECT_USER_ROLE, dbIndex), new Object[] { userId }, Role.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return list;
	}

	public Role selectRole(int roleId) {
		Role list = null;
		try {
			list = (Role) JDBCManager.INSTANCE.getConn("part0").excuteObject(SELECT_ROLE, new Object[] { roleId },
					Role.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return list;
	}

	public void insertRole(Role role) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(INSERT_USER_ROLE,
				new Object[] { role.getUserId(), role.getId(), role.getOccupationId(), role.getName(),
						role.getCapacity(), role.getTeamId(), role.getExp(), role.getLevel(), role.getLastJoinScene(),
						role.getAttributeStr(), role.getGuildId() });

	}

	public void updateRoleGuild(int roleId, int userId, int guildId) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_USER_GUILD,
				new Object[] { guildId, roleId, userId });

	}

	public void updateRoleSkill(RoleSkill roleSkill) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ROLE_SKILL, new Object[] {
				roleSkill.getRoleId(), roleSkill.getSkillId(), roleSkill.getLevel(), roleSkill.getLastUsedTime() });
	}

	public void updateRoleTeam(int roleId, int teamId) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ROLE_TEAM_STATUS,
				new Object[] { teamId, roleId });
	}

	public List<RoleSkill> getRoleSkill(int roleId) {
		List<RoleSkill> list = null;
		try {
			list = (List<RoleSkill>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_SKILL,
					new Object[] { roleId }, RoleSkill.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return list;
	}

	public void updateRole(Role role) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_USER_ROLE,
				new Object[] { role.getUserId(), role.getId(), role.getOccupationId(), role.getName(),
						role.getCapacity(), role.getTeamId(), role.getExp(), role.getLevel(), role.getLastJoinScene(),
						role.getAttributeStr(), role.getGuildId() });
	}
}
