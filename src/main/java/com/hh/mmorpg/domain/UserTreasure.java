package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserTreasure extends Material {

	public UserTreasure(int roleId, String name, int id, int quantity) {
		super(roleId, name, id, MaterialType.TREASURE_TYPE_ID, quantity);
		// TODO Auto-generated constructor stub
	}

	public static final ResultBuilder<UserTreasure> BUILDER = new ResultBuilder<UserTreasure>() {

		@Override
		public UserTreasure build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt("roleId");
			int id = result.getInt("treasureId");
			String name = result.getString("name");
			int quantity = result.getInt("quantity");
			return new UserTreasure(roleId, name, id, quantity);
		}
	};
}
