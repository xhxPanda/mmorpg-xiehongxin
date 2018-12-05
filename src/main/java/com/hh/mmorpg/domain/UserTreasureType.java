package com.hh.mmorpg.domain;

public enum UserTreasureType {
	GOLD_COIN(1, "金币"), 
	YUAN_BAO(2, "元宝");

	private int id;
	private String name;

	UserTreasureType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static UserTreasureType getUserTreasureType(int userTreasureId) {
		for (UserTreasureType type : UserTreasureType.values()) {
			if (type.getId() == userTreasureId) {
				return type;
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
