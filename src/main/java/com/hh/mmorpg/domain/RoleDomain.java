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

		// 组建角色初始属性
		for (String strList : attributeStr.split(",")) {
			String str[] = strList.split(":");
			int attributeId = Integer.parseInt(str[0]);
			int attributeValue = Integer.parseInt(str[1]);

			AttributeEnum attributeEnum = AttributeEnum.getUserTreasureType(attributeId);

			Attribute attribute = new Attribute(attributeId, attributeValue, attributeEnum.getName());
			attributeMap.put(attribute.getId(), attribute);
		}

		// 组建用户技能
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
