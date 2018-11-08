package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

public class RoleDomain {

	private int id;
	private String name;
	private Map<Integer, Attribute> attributeMap;
	private Map<Integer, RoleSkill> roleSkillMap;

	public RoleDomain(int id, String name, String attributeStr, String skillStr) {
		this.id = id;
		this.name = name;
		this.attributeMap = new HashMap<>();

		for (String strList : attributeStr.split(",")) {
			String str[] = strList.split(":");
			Attribute attribute = new Attribute(Integer.parseInt(str[0]), Integer.parseInt(str[2]), str[1],
					Integer.parseInt(str[3]));
			attributeMap.put(attribute.getId(), attribute);
		}

		this.roleSkillMap = new HashMap<>();
		for (String strList : skillStr.split(",")) {
			int skillId = Integer.parseInt(strList);
			roleSkillMap.put(skillId, new RoleSkill(skillId));
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<Integer, Attribute> getAttributeMap() {
		return attributeMap;
	}

	public Map<Integer, RoleSkill> getRoleSkillMap() {
		return roleSkillMap;
	}

}
