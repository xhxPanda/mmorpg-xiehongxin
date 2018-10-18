package com.hh.mmorpg.domain;

import java.util.HashMap;

public class MonsterDomain {

	private int id;
	private String name;
	private int freshTime;
	private HashMap<Integer, Attribute> attributeMap;

	public MonsterDomain(int id, String name, int freshTime, String attributeStr) {
		this.id = id;
		this.name = name;
		this.freshTime = freshTime;

		this.attributeMap = new HashMap<>();
		for (String attributeDomain : attributeStr.split(",")) {
			String str[] = attributeDomain.split(":");

			Attribute attribute = new Attribute(Integer.parseInt(str[0]), Integer.parseInt(str[0]), str[1]);
			attributeMap.put(attribute.getId(), attribute);
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getFreshTime() {
		return freshTime;
	}

	public HashMap<Integer, Attribute> getAttributeMap() {
		return attributeMap;
	}

}
