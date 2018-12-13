package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 
 * @author xhx 财富
 */
public class UserTreasure extends Material {

	private long quantity;

	public UserTreasure(int roleId, String name, int id, long quantity) {
		super(roleId, id, name, MaterialType.TREASURE_TYPE.getId());
		this.quantity = quantity;
	}

	public long changeQuantity(long value) {
		return quantity += value;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public static final ResultBuilder<UserTreasure> BUILDER = new ResultBuilder<UserTreasure>() {

		@Override
		public UserTreasure build(ResultSet result) throws SQLException {
			int roleId = result.getInt("roleId");
			int id = result.getInt("id");
			String name = result.getString("name");
			long quantity = result.getLong("quantity");
			return new UserTreasure(roleId, name, id, quantity);
		}
	};
}
