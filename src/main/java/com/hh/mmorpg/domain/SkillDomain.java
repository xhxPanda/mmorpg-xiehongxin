package com.hh.mmorpg.domain;

import java.util.Map;

/**
 * 
 * @author xhx 技能定义实体类
 */

public class SkillDomain {

	private int id;
	private int cd;
	private String name;
	private int needMp;
	private boolean magicSkill;
	private int needLevel;
	private int occupationId;

	private int skillType;

	private String summon;

	private Map<Integer, Integer> effectAttribute;
	private Map<Integer, Integer> selfEffectAttribute;
	private Map<Integer, Integer> buffprobabilityMap;

	public SkillDomain(int id, String name, int cd, boolean magicSkill, int needLevel, int occupationId,
			Map<Integer, Integer> effectAttribute, Map<Integer, Integer> selfEffectAttribute,
			Map<Integer, Integer> buffprobabilityMap, int needMp, int skillType, String summon) {
		this.id = id;
		this.name = name;
		this.cd = cd;
		this.magicSkill = magicSkill;
		this.needLevel = needLevel;
		this.occupationId = occupationId;
		this.selfEffectAttribute = selfEffectAttribute;
		this.effectAttribute = effectAttribute;
		this.buffprobabilityMap = buffprobabilityMap;
		this.needMp = needMp;
		this.skillType = skillType;
		this.summon = summon;
	}

	public int getId() {
		return id;
	}

	public int getCd() {
		return cd;
	}

	public Map<Integer, Integer> getBuffprobabilityMap() {
		return buffprobabilityMap;
	}

	public Map<Integer, Integer> getSelfEffectAttribute() {
		return selfEffectAttribute;
	}

	public Map<Integer, Integer> getEffectAttribute() {
		return effectAttribute;
	}

	public String getName() {
		return name;
	}

	public int getNeedMp() {
		return needMp;
	}

	public boolean isMagicSkill() {
		return magicSkill;
	}

	public int getNeedLevel() {
		return needLevel;
	}

	public int getOccupationId() {
		return occupationId;
	}

	public int getSkillType() {
		return skillType;
	}

	public String getSummon() {
		return summon;
	}

	@Override
	public String toString() {
		return "SkillDomain [id=" + id + ", name=" + name + "]";
	}

}
