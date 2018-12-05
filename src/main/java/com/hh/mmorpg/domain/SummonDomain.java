package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

public class SummonDomain {

	private int id;
	private String name;
	private int terminalTime;
	private Map<Integer, RoleSkill> skillMap; // 技能库
	private Map<Integer, Attribute> attributeMap;

	public SummonDomain(int id, String name, int terminalTime, String skillsStr, String attributeStr) {
		this.id = id;
		this.name = name;
		this.terminalTime = terminalTime;
		
		this.attributeMap = new HashMap<>();
		for (String attributeDomain : attributeStr.split(",")) {
			String str[] = attributeDomain.split(":");
			int attributeId = Integer.parseInt(str[0]);
			int attributeValue = Integer.parseInt(str[1]);
			AttributeEnum attributeEnum = AttributeEnum.getUserTreasureType(attributeId);

			Attribute attribute = new Attribute(attributeId, attributeValue, attributeEnum.getName());
			this.attributeMap.put(attribute.getId(), attribute);
		}

		this.skillMap = new HashMap<>();
		for (String strList : skillsStr.split(",")) {
			int skillId = Integer.parseInt(strList);
			this.skillMap.put(skillId, new RoleSkill(id, skillId, 1));
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getTerminalTime() {
		return terminalTime;
	}

	public Map<Integer, RoleSkill> getSkillMap() {
		return skillMap;
	}

	public Map<Integer, Attribute> getAttributeMap() {
		return attributeMap;
	}

}
