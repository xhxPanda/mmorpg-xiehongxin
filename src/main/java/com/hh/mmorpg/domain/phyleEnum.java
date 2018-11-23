package com.hh.mmorpg.domain;

public enum phyleEnum {

	HUMER(1, "人族"), 
	BEAST(1, "兽族");

	private int id;
	private String name;

	phyleEnum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static phyleEnum getPhyleEnum(int id) {
		for (phyleEnum type : phyleEnum.values()) {
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
