package com.hh.mmorpg.domain;

public class EquimentDomain {

	private int id;
	private int clothesType;
	private String attributes;
	private String name;
	private int maxDurability;

	public EquimentDomain(int id, int clothesType, String attributes, String name, int maxDurability) {
		this.id = id;
		this.clothesType = clothesType;
		this.attributes = attributes;
		this.name = name;
		this.maxDurability = maxDurability;
	}

	public int getId() {
		return id;
	}

	public int getClothesType() {
		return clothesType;
	}

	public String getAttributes() {
		return attributes;
	}

	public String getName() {
		return name;
	}

	public int getMaxDurability() {
		return maxDurability;
	}

}
