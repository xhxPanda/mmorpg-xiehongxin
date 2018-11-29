package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserItem {

	
	private int roleId;
	private int materialId;
	private long lastUsedTime;

	public UserItem(int roleId, int materialId, long lastUsedTime) {
		this.roleId = roleId;
		this.materialId = materialId;
		this.lastUsedTime = lastUsedTime;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getMaterialId() {
		return materialId;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}
	
	public static final ResultBuilder<UserItem> BUILDER = new ResultBuilder<UserItem>() {
		
		@Override
		public UserItem build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			
			int roleId = result.getInt("roleId");
			int materialId = result.getInt("materialId");
			long lastUsedTime = result.getLong("lastUsedTime");
			return new UserItem(roleId, materialId, lastUsedTime);
		}
	};
}
