package com.hh.mmorpg.counter;

public class CounterResult {

	private int roleId;
	private String key;
	private int record;

	public CounterResult(int roleId, String key, int record) {
		this.roleId = roleId;
		this.key = key;
		this.record = record;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getKey() {
		return key;
	}

	public int getRecord() {
		return record;
	}

}
