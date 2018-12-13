package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class RoleSkill {

	private int roleId;
	private int skillId;
	private long lastUsedTime;
	private int level;

	public RoleSkill(int roleId, int skillId, int level) {
		this.roleId = roleId;
		this.skillId = skillId;
		this.lastUsedTime = 0;
		this.level = level;
	}

	public RoleSkill(int roleId, int skillId, long lastUsedTime, int level) {
		this.roleId = roleId;
		this.skillId = skillId;
		this.lastUsedTime = lastUsedTime;
		this.level = level;
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

	public void addLevel() {
		this.level += 1;
	}

	public int getLevel() {
		return level;
	}

	public static final ResultBuilder<RoleSkill> BUILDER = new ResultBuilder<RoleSkill>() {

		@Override
		public RoleSkill build(ResultSet result) throws SQLException {
			int roleId = result.getInt("roleId");
			int skillId = result.getInt("skillId");
			long lastUsedTime = result.getLong("lastUsedTime");
			int level = result.getInt("level");
			return new RoleSkill(roleId, skillId, lastUsedTime, level);
		}
	};
}
