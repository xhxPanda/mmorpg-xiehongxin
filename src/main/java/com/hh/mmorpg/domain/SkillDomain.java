package com.hh.mmorpg.domain;

import java.util.Map;

public class SkillDomain {

	private int id;
	private int cd;
	private String name;
	private int needMp;
	private Map<Integer, Integer> effectAttribute;
	private Map<Integer, Integer> selfEffectAttribute;
	private Map<Integer, Integer> buffprobabilityMap;

	public SkillDomain(int id, String name, int cd, Map<Integer, Integer> effectAttribute,
			Map<Integer, Integer> selfEffectAttribute, Map<Integer, Integer> buffprobabilityMap, int needMp) {
		this.id = id;
		this.name = name;
		this.cd = cd;
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

}
