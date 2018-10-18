package com.hh.mmorpg.domain;

public class RoleSkill {

	private int skillId;
	private long lastUsedTime;

	public RoleSkill(int skillId) {
		this.skillId = skillId;
		this.lastUsedTime = 0;
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

}
