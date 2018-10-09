package com.hh.mmorpg.server.role;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.jdbc.JDBCManager;

public class RoleDao {

	public static final RoleDao INSTANCE = new RoleDao();

	private static final int DB_INDEX = 1000 * 10000;

	private static final String SELECT_USER_ROLE = "SELECT * FROM role%s where userId = ?";

	private static final String INSERT_USER_ROLE = "INSERT INTO role%s('userId','roleId', `name`) values (?, ?, ?) ";

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

	public int insertRole(Role role) {
		int dbIndex = role.getUserId() / DB_INDEX;
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(String.format(INSERT_USER_ROLE, dbIndex),
				new Object[] { role.getUserId(), role.getId(), role.getRoleId(), role.getName() });

	}
}
