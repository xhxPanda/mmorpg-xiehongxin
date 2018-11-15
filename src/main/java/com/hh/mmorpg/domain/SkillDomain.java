package com.hh.mmorpg.domain;

import java.util.Map;

/**
 * 
 * @author xhx
 * 技能定义实体类
 */

public class SkillDomain {

	private int id;
	private int cd;
	private String name;
	private int needMp;
	private boolean magicSkill;
	private Map<Integer, Integer> effectAttribute;
	private Map<Integer, Integer> selfEffectAttribute;
	private Map<Integer, Integer> buffprobabilityMap;

	public SkillDomain(int id, String name, int cd, boolean magicSkill, Map<Integer, Integer> effectAttribute,
			Map<Integer, Integer> selfEffectAttribute, Map<Integer, Integer> buffprobabilityMap, int needMp) {
		this.id = id;
		this.name = name;
		this.cd = cd;
		this.magicSkill = magicSkill;
		this.selfEffectAttribute = selfEffectAttribute;
		this.effectAttribute = effectAttribute;
		this.buffprobabilityMap = buffprobabilityMap;
		this.needMp = needMp;
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

}
