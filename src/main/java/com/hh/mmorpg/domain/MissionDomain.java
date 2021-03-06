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
	private boolean isAchievement; // 是不是成就类型的

	private Map<String, MissionAttribute> competeAttribute;

	public MissionDomain(int id, String name, String bonus, String dec, int type, int needLevel,
			String competeAttributeStr, boolean isAchievement) {
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
			String processDec = attributes[0];
			String key = attributes[1];
			int value = Integer.parseInt(attributes[2]);
			competeAttribute.put(key, new MissionAttribute(key, processDec, 0, value));
		}
		this.isAchievement = isAchievement;
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

	public Map<String, MissionAttribute> getCompeteAttribute() {
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

	public boolean isAchievement() {
		return isAchievement;
	}

	@Override
	public String toString() {
		return "任务 [id=" + id + ", 名称=" + name + ", 任务描述=" + dec + "]";
	}
}
