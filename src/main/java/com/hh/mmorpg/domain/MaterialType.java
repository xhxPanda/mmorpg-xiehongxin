package com.hh.mmorpg.domain;

public enum MaterialType {
	EQUIPMENT_TYPE(1, "武器"), ITEM_TYPE(2, "道具"), TREASURE_TYPE(3, "财富"), EXP_TYPE(4, "经验");

	private int id;
	private String name;

	private MaterialType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static MaterialType getMaterialType(int id) {
		for (MaterialType type : MaterialType.values()) {
			if (type.getId() == id) {
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
