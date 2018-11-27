package com.hh.mmorpg.domain;

public enum OccupationEmun {
	
	NONE(-1, "无职业"), 
	MASTER(1, "法师"), 
	SUMMONER(2, "召唤师"), 
	WARRIOR(3, "战士");

	private int id;
	private String name;

	private OccupationEmun(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static OccupationEmun getOccupationEmun(int id) {
		for (OccupationEmun occupationEmun : OccupationEmun.values()) {
			if (occupationEmun.getId() == id) {
				return occupationEmun;
			}
		}
		return null;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
