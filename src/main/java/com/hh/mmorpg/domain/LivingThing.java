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
		this.status = true;
		this.beKilledTime = 0;
	}

	public void addBuff(RoleBuff buff) {
		buffsMap.put(buff.getBuffId(), buff);
		afterBuffAdd(buff);
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
			if (!status || buff.isExpire(now)) {
				needRemoveBuff.add(buff);
				continue;
			}

			if ((now - buff.getLastUsedTime()) < buff.getHeartbeatTime()) {
				continue;
			}

			buff.setLastUsedTime(System.currentTimeMillis());
			for (Entry<Integer, Integer> entry : buff.getEffectValue().entrySet()) {
				if (!buff.isBuff()) {
					effectAttribute(entry.getKey(), entry.getValue(), buff.getName() + buff.getName() + "buff作用");
				} else {
					effectAttribute(entry.getKey(), -entry.getValue(), buff.getName() +  buff.getName() + "buff作用");
				}

				buff.setLastUsedTime(now);
			}
		}

		for (RoleBuff roleBuff : needRemoveBuff) {
			if (roleBuff.isResore()) {
				for (Entry<Integer, Integer> entry : roleBuff.getEffectValue().entrySet()) {
					if (!roleBuff.isBuff()) {
						effectAttribute(entry.getKey(), -entry.getValue(), roleBuff.getName() +  roleBuff.getName() + "buff移除");
					} else {
						effectAttribute(entry.getKey(), entry.getValue(), roleBuff.getName() +  roleBuff.getName() + "buff作用");
					}
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

	public int effectAttribute(int key, int value, String reason) {
		Attribute attribute = attributeMap.get(key);

		if (attribute.getId() == AttributeEnum.HP.getId()) {
			if (attribute.getValue() == attributeMap.get(AttributeEnum.MAX_HP.getId()).getValue()) {
				return attribute.getValue();
			}
		}
		if (attribute.getId() == AttributeEnum.MP.getId()) {
			if (attribute.getValue() == attributeMap.get(AttributeEnum.MAX_MP.getId()).getValue()) {
				return attribute.getValue();
			}
		}

		int oldValue = attribute.getValue();

		int newValue = attribute.changeValue(value);
		if (isDead()) {
			afterDead();
		}

		if (attribute.getId() == AttributeEnum.MAX_MP.getId()
				&& attributeMap.get(AttributeEnum.MP.getId()).getValue() == attribute.getValue()) {
			effectAttribute(AttributeEnum.MP.getId(), newValue, "最大mp改变");
		}

		if (attribute.getId() == AttributeEnum.MAX_HP.getId()
				&& attributeMap.get(AttributeEnum.HP.getId()).getValue() == attribute.getValue()) {
			effectAttribute(AttributeEnum.HP.getId(), newValue, "最大mp改变");
		}
		
		if (oldValue != newValue) {
			notifyAttributeChange(attribute, reason);
		}

		return newValue;
	}

	public void resurrection() {
		Attribute hp = attributeMap.get(AttributeEnum.HP.getId());
		Attribute mp = attributeMap.get(AttributeEnum.MP.getId());

		hp.changeValue(attributeMap.get(AttributeEnum.MAX_HP.getId()).getValue());
		mp.changeValue(attributeMap.get(AttributeEnum.MAX_MP.getId()).getValue());
	}

	public abstract void afterDead();

	public abstract void notifyAttributeChange(Attribute attribute, String reason);

	public abstract void afterBuffAdd(RoleBuff roleBuff);

}
