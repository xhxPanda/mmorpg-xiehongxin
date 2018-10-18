package com.hh.mmorpg.server.clothes;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.UserClothes;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.jdbc.JDBCManager;

public class RoleClothesDao {

	public static final RoleClothesDao INSTANCE = new RoleClothesDao();

	private RoleClothesDao() {
	}

	private static final String SELECT_ROLE_CLOTHES = "SELECT * FROM roleclothes%s WHERE roleId = ?";

	@SuppressWarnings("unchecked")
	public List<UserClothes> getAllUserClothes(int roleId) {
		List<UserClothes> list = null;
		try {
			list = (List<UserClothes>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(String.format(SELECT_ROLE_CLOTHES, 0), new Object[] { roleId }, UserClothes.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

}
