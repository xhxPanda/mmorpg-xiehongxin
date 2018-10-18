package com.hh.mmorpg.server.item;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.jdbc.JDBCManager;

public class RoleItemDao {

	public static final RoleItemDao INSTANCE = new RoleItemDao();

	private static final String GET_ALL_ITEM = "SELECT * FROM roleItem0 WHERE roleId = ?";

	@SuppressWarnings("unchecked")
	public List<UserItem> getAllItem(int roleId) {
		List<UserItem> list = null;
		try {
			list = (List<UserItem>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(String.format(GET_ALL_ITEM, 0), new Object[] { roleId }, UserItem.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

}
