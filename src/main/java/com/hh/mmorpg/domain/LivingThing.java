package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LivingThing {

	private int id;
	private int uniqueId;
	private long beKilledTime;
	private boolean status;
	private Map<Integer, RoleSkill> skillMap;
	private Map<Integer, Attribute> attributeMap;

	private ConcurrentHashMap<Integer, RoleBuff> buffsMap;

	public LivingThing(int id, int uniqueId) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.skillMap = new HashMap<>();
		this.attributeMap = new HashMap<>();
		this.buffsMap = new ConcurrentHashMap<>();
		this.buffsMap = new ConcurrentHashMap<>();
		this.status = true;
		this.beKilledTime = 0;
	}

	public void addBuff(RoleBuff buff) {
		buffsMap.put(buff.getBuffId(), buff);
	}

	public Attribute getAttribute(int id) {
		return attributeMap.get(id);
	}

	public Map<Integer, RoleSkill> getSkillMap() {
		return skillMap;
	}

	public void setSkillMap(Map<Integer, RoleSkill> skillMap) {
		this.skillMap = skillMap;
	}

	public RoleSkill getRoleSkill(int roleSkillId) {
		return skillMap.get(roleSkillId);
	}

	public boolean isHasSkill(int skillId) {
		return skillMap.containsKey(skillId);
	}

	public void setAttributeMap(Map<Integer, Attribute> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public Map<Integer, Attribute> getAttributeMap() {
		return attributeMap;
	}

	public long getBeKilledTime() {
		return beKilledTime;
	}

	public void setBeKilledTime(long beKilledTime) {
		this.beKilledTime = beKilledTime;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public void takeEffect() {
		List<RoleBuff> needRemoveBuff = new ArrayList<RoleBuff>();
		long now = System.currentTimeMillis();
		for (RoleBuff buff : buffsMap.values()) {
			if (!status || buff.isExpire(now) ) {
				needRemoveBuff.add(buff);
				continue;
			}
			
			if((now - buff.getLastUsedTime()) < buff.getHeartbeatTime()) {
				continue;
			}

			buff.setLastUsedTime(System.currentTimeMillis());
			for (Entry<Integer, Integer> entry : buff.getEffectValue().entrySet()) {
				if(buff.isBuff()) {
					effectAttribute(entry.getKey(), -entry.getValue());
				} else {
					effectAttribute(entry.getKey(), entry.getValue());
				}
				
				buff.setLastUsedTime(now);
			}
		}

		for (RoleBuff roleBuff : needRemoveBuff) {
			if (roleBuff.isResore()) {
				for (Entry<Integer, Integer> entry : roleBuff.getEffectValue().entrySet()) {
					resoreAttribute(entry.getKey(), entry.getValue(), roleBuff.isBuff());
				}
			}
			buffsMap.remove(roleBuff.getBuffId());
		}
	}

	public boolean isDead() {
		return getHp() == 0;
	}

	public int getHp() {
		return attributeMap.get(3).getValue();
	}

	public int getMp() {
		return attributeMap.get(4).getValue();
	}

	public int effectAttribute(int key, int value) {
		Attribute attribute = attributeMap.get(key);

		int newValue = attribute.changeValue(value);
		if (isDead()) {
			afterDead();
		}

		return newValue;
	}

	public void resurrection() {
		attributeMap.get(4);
	}

	public void resoreAttribute(int key, int value, boolean isBuff) {
		Attribute attribute = attributeMap.get(key);
		attribute.changeValue(value);
	}

	public abstract void afterDead();
	
}
