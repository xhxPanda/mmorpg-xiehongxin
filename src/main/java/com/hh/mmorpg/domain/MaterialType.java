package com.hh.mmorpg.domain;

public enum MaterialType {
	EQUIPMENT_TYPE(1, "武器"), 
	ITEM_TYPE(2, "道具"), 
	TREASURE_TYPE(3, "财富"),
	EXP_TYPE(4, "经验");
	
	private int id;
	private String name;
	
	private MaterialType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	
	

}
