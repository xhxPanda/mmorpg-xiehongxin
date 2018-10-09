package com.hh.mmorpg.domain;

public class Monster extends NpcRole {

	private int uniqueId;
	private int freshTime;
	private long beKilledTime;
	private int status;

	public Monster(int id, int uniqueId, String name, int freshTime) {
		super(id, name);
		this.uniqueId = uniqueId;
		this.freshTime = freshTime;
	}

	public long getBeKilledTime() {
		return beKilledTime;
	}

	public void setBeKilledTime(long beKilledTime) {
		this.beKilledTime = beKilledTime;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public int getFreshTime() {
		return freshTime;
	}

	public int getStatus() {
		return status;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Monster [uniqueId=" + uniqueId + ", beKilledTime=" + beKilledTime + ", status=" + status + ", name=" + name +  "]";
	}


}
