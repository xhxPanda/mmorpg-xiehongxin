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
	private Map<Integer, Integer> competeAttribute;

	public MissionDomain(int id, String name, String bonus, String competeAttributeStr) {
		super();
		this.id = id;
		this.name = name;
		this.bonus = bonus;
		
		this.competeAttribute = new HashMap<>();
		for (String s : competeAttributeStr.split(",")) {
			String attributes[] = s.split(":");
			int key = Integer.parseInt(attributes[0]);
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

	public Map<Integer, Integer> getCompeteAttribute() {
		return competeAttribute;
	}

}
