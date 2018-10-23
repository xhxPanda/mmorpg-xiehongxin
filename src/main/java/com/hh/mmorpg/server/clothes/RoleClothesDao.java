package com.hh.mmorpg.server.clothes;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.jdbc.JDBCManager;

public class RoleClothesDao {

	public static final RoleClothesDao INSTANCE = new RoleClothesDao();

	private RoleClothesDao() {
	}

	private static final String SELECT_ROLE_CLOTHES = "SELECT * FROM roleclothes%s WHERE roleId = ?";

	@SuppressWarnings("unchecked")
	public List<UserEquipment> getAllUserClothes(int roleId) {
		List<UserEquipment> list = null;
		try {
			list = (List<UserEquipment>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(String.format(SELECT_ROLE_CLOTHES, 0), new Object[] { roleId }, UserEquipment.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

}
