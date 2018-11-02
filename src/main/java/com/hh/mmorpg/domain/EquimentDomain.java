package com.hh.mmorpg.domain;

public class EquimentDomain {

	private int id;
	private int clothesType;
	private String attributes;
	private String name;
	private int maxDurability;
	private String sellPrice;

	public EquimentDomain(int id, int clothesType, String attributes, String name, int maxDurability, String sellPrice) {
		this.id = id;
		this.clothesType = clothesType;
		this.attributes = attributes;
		this.name = name;
		this.maxDurability = maxDurability;
		this.sellPrice = sellPrice;
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

	public String getSellPrice() {
		return sellPrice;
	}

}
