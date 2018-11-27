package com.hh.mmorpg.domain;

public class TeamMate {

	private int roleId;
	private String name;
	private OccupationEmun occupation;
	private boolean online;

	public TeamMate(int roleId, String name, OccupationEmun occupation, boolean online) {
		this.roleId = roleId;
		this.name = name;
		this.occupation = occupation;
		this.online = online;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getName() {
		return name;
	}

	public OccupationEmun getOccupationName() {
		return occupation;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public String toString() {
		return "TeamMate [角色id=" + roleId + ", 角色名称=" + name + ", 职业=" + occupation.getName() + ", 是否在线=" + online
				+ "]";
	}

}
