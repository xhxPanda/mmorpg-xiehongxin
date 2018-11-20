package com.hh.mmorpg.event.data;

import com.hh.mmorpg.event.EventData;

public class UpdateLevelData extends EventData {

	private int oldLevel;
	private int newLevel;
	private int userId;
	private int roleId;

	public UpdateLevelData(int oldLevel, int newLevel, int userId, int roleId) {
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
		this.userId = userId;
		this.roleId = roleId;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public int getUserId() {
		return userId;
	}

	public int getRoleId() {
		return roleId;
	}

}
