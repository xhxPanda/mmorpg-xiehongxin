package com.hh.mmorpg.event.data;

import com.hh.mmorpg.event.EventData;

public class RoleChangeData extends EventData {

	private int userId;
	private int oldRoleId;
	private int newRoleId;
	public RoleChangeData(int userId, int oldRoleId, int newRoleId) {
		this.userId = userId;
		this.oldRoleId = oldRoleId;
		this.newRoleId = newRoleId;
	}
	public int getUserId() {
		return userId;
	}
	public int getOldRoleId() {
		return oldRoleId;
	}
	public int getNewRoleId() {
		return newRoleId;
	}

}
