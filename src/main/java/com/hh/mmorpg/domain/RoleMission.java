package com.hh.mmorpg.domain;

public class RoleMission {

	private int missionId;
	private int roleId;
	private int status;

	public RoleMission(int missionId, int roleId, int status) {
		this.missionId = missionId;
		this.roleId = roleId;
		this.status = status;
	}

	public int getMissionId() {
		return missionId;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getStatus() {
		return status;
	}

}
