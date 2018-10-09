package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class Role {

	private int userId;
	private int id;
	private String name;
	private int roleId;

	public Role(int userId, int id, String name, int roleId) {
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.roleId = roleId;
	}

	public int getUserId() {
		return userId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getRoleId() {
		return roleId;
	}

	public static final ResultBuilder<Role> BUILDER = new ResultBuilder<Role>() {

		@Override
		public Role build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int userId = result.getInt(1);
			int id = result.getInt(2);
			int roleId = result.getInt(3);
			String name = result.getString(4);
			return new Role(userId, id, name, roleId);
		}
	};

	@Override
	public String toString() {
		return "Role [userId=" + userId + ", id=" + id + ", name=" + name + "]";
	}

}
