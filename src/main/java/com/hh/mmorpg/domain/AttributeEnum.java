package com.hh.mmorpg.domain;

public enum AttributeEnum {

	Aggressivity(1, "攻击力"), 
	DEFEMSES(2, "防御力"), 
	HP(3, "hp"), 
	MP(4, "mp"), 
	MAX_HP(5, "最大hp"), 
	MAX_MP(6, "最大mp");

	private int id;
	private String name;

	private AttributeEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static AttributeEnum getUserTreasureType(int attributeId) {
		for (AttributeEnum attributeEnum : AttributeEnum.values()) {
			if (attributeEnum.getId() == attributeId) {
				return attributeEnum;
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
