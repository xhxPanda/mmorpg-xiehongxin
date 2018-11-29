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
	private static final String SELECT_ROLE = "SELECT * FROM role0 where roleId = ?";
	private static final String INSERT_USER_ROLE = "INSERT INTO role%s('userId', 'id', `tribeId`, `occupationId`, `name`, `capacity`, `guildId`, `teamId`, `attribute`, `exp`, `Level`, `lastInSceneId`) values (?, ?, ?, ? , ?, ?, ?, ? ,?, ?, ?, ?) ";

	private static final String UPDATE_ROLE_SKILL = "REPLACE INTO RoleSkill('roleId', 'SkillId', `LastUsedTime`) values (?, ?, ?) ";
	private static final String SELECT_ROLE_SKILL = "SELECT * FROM RoleSkill where roleId = ?";

	private static final String UPDATE_USER_GUILD = "UPDATE role%s SET guildId = ? WHERE roleId = ? AND UserId = ?";

	private static final String UPDATE_USER_ROLE = "REPLACE INTO role%s('userId', 'id', `tribeId`, `occupationId`, `name`, `capacity`, `guildId`, `teamId`, `attribute`, `exp`, `Level`, `lastInSceneId`) values (?, ?, ?, ? , ?, ?, ?, ? ,?, ?, ?, ?)";

	private static final String UPDATE_ROLE_TEAM_STATUS = "UPDATE role%s SET TeamId = ? WHERE roleId = ?";

	@SuppressWarnings("unchecked")
	public List<Role> selectUserRole(int userId) {
		int dbIndex = userId / DB_INDEX;
		List<Role> list = null;
		try {
			list = (List<Role>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(String.format(SELECT_USER_ROLE, dbIndex), new Object[] { userId }, Role.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public int insertRole(Role role) {
		int dbIndex = role.getUserId() / DB_INDEX;
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(String.format(INSERT_USER_ROLE, dbIndex),
				new Object[] { role.getUserId(), role.getId(), role.getTribeId(), role.getTribeId(), role.getName(),
						role.getCapacity(), role.getGuildId(), role.getTeamId(), role.getAttributeMap(), role.getExp(),
						role.getLevel(), role.getLastJoinScene() });

	}

	public int updateRoleGuild(int roleId, int userId, int guildId) {
		int dbIndex = userId / DB_INDEX;
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(String.format(UPDATE_USER_GUILD, dbIndex),
				new Object[] { guildId, roleId, userId });

	}

	public int updateRoleSkill(RoleSkill roleSkill) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ROLE_SKILL,
				new Object[] { roleSkill.getRoleId(), roleSkill.getSkillId(), roleSkill.getLastUsedTime() });
	}

	public int updateRoleTeam(int roleId, int teamId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ROLE_TEAM_STATUS,
				new Object[] { teamId, roleId });
	}

	@SuppressWarnings("unchecked")
	public List<RoleSkill> getRoleSkill(int roleId) {
		List<RoleSkill> list = null;
		try {
			list = (List<RoleSkill>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_SKILL,
					new Object[] { roleId }, RoleSkill.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public int updateRole(Role role) {
		int dbIndex = role.getUserId() / DB_INDEX;
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(String.format(UPDATE_USER_ROLE, dbIndex),
				new Object[] { role.getUserId(), role.getId(), role.getTribeId(), role.getTribeId(), role.getName(),
						role.getCapacity(), role.getGuildId(), role.getTeamId(), role.getAttributeMap(), role.getExp(),
						role.getLevel(), role.getLastJoinScene() });
	}
}
