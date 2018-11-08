package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author xhx 怪物定义实体类
 */

public class MonsterDomain {

	private int id;
	private String name;
	private int freshTime;
	private HashMap<Integer, Attribute> attributeMap;
	private Map<String, Integer> killFallItemMap;
	private Map<Integer, RoleSkill> roleSkillMap;

	public MonsterDomain(int id, String name, int freshTime, String attributeStr, String killFallItemStr,
			String skillsStr) {
		this.id = id;
		this.name = name;
		this.freshTime = freshTime;

		this.attributeMap = new HashMap<>();
		for (String attributeDomain : attributeStr.split(",")) {
			String str[] = attributeDomain.split(":");

			Attribute attribute = new Attribute(Integer.parseInt(str[0]), Integer.parseInt(str[2]), str[1], Integer.parseInt(str[2]));
			this.attributeMap.put(attribute.getId(), attribute);
		}

		this.killFallItemMap = new HashMap<>();
		String fallStrList[] = killFallItemStr.split(",");
		for (String s : fallStrList) {
			String item = s.split("\\$")[0];
			int possibility = Integer.parseInt(s.split("\\$")[1]);
			this.killFallItemMap.put(item, possibility);
		}

		this.roleSkillMap = new HashMap<>();
		for (String strList : skillsStr.split(",")) {
			int skillId = Integer.parseInt(strList);
			this.roleSkillMap.put(skillId, new RoleSkill(skillId));
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

	public Map<String, Integer> getKillFallItemMap() {
		return killFallItemMap;
	}

	public Map<Integer, RoleSkill> getRoleSkillMap() {
		return roleSkillMap;
	}

}
