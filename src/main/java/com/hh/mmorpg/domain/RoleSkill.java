package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class RoleSkill {

	private int roleId;
	private int skillId;
	private long lastUsedTime;

	public RoleSkill(int roleId, int skillId) {
		this.roleId = roleId;
		this.skillId = skillId;
		this.lastUsedTime = 0;
	}

	public RoleSkill(int roleId, int skillId, long lastUsedTime) {
		this.roleId = roleId;
		this.skillId = skillId;
		this.lastUsedTime = lastUsedTime;
	}

	public int getSkillId() {
		return skillId;
	}

	public long getLastUseTime() {
		return lastUsedTime;
	}

	public void setLastUseTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public int getRoleId() {
		return roleId;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public static final ResultBuilder<RoleSkill> BUILDER = new ResultBuilder<RoleSkill>() {

		@Override
		public RoleSkill build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt(1);
			int skillId = result.getInt(2);
			long lastUsedTime = result.getLong(3);
			return new RoleSkill(roleId, skillId, lastUsedTime);
		}
	};
}
