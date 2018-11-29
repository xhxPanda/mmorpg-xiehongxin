package com.hh.mmorpg.domain;

/**
 * 武器实体
 * 
 * @author xhx
 *
 */
public class EquimentDomain {

	private int id;
	private int equimentType;
	private String attributes;
	private String name;
	private int maxDurability;
	private String sellPrice;

	private int equimentLevel; // 武器等级
	private int equimentSource; // 武器评分

	public EquimentDomain(int id, int equimentType, String attributes, String name, int maxDurability, String sellPrice,
			int equimentLevel, int equimentSource) {
		this.id = id;
		this.equimentType = equimentType;
		this.attributes = attributes;
		this.name = name;
		this.maxDurability = maxDurability;
		this.sellPrice = sellPrice;
		this.equimentLevel = equimentLevel;
		this.equimentSource = equimentSource;
	}

	public int getEquimentType() {
		return equimentType;
	}

	public int getId() {
		return id;
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

	public int getEquimentLevel() {
		return equimentLevel;
	}

	public int getEquimentSource() {
		return equimentSource;
	}

}
