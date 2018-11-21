package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务定义类
 */
public class MissionDomain {

	private int id;
	private String name;
	private String bonus;
	private String dec;

	private int type; // 任务类型
	private int needLevel; // 所需等级

	private Map<String, Integer> competeAttribute;

	public MissionDomain(int id, String name, String bonus, String dec, int type, int needLevel, String competeAttributeStr) {
		super();
		this.id = id;
		this.name = name;
		this.bonus = bonus;
		this.dec = dec;

		this.type = type;
		this.needLevel = needLevel;

		this.competeAttribute = new HashMap<>();
		for (String s : competeAttributeStr.split(",")) {
			String attributes[] = s.split(":");
			String key = attributes[0];
			int value = Integer.parseInt(attributes[1]);
			competeAttribute.put(key, value);
		}

	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getBonus() {
		return bonus;
	}

	public Map<String, Integer> getCompeteAttribute() {
		return competeAttribute;
	}

	public int getNeedLevel() {
		return needLevel;
	}

	public int getType() {
		return type;
	}

	public String getDec() {
		return dec;
	}

}
