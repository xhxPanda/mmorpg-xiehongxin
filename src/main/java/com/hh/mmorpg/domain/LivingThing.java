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
	private String name;
	private long beKilledTime;
	private boolean status;
	private Map<Integer, RoleSkill> skillMap; // 技能库
	private Map<Integer, Attribute> attributeMap;
	private int sceneId;

	/**
	 * 对于怪物来说，这个参数代表拉动它愤怒值最高的人，主要攻击那个人 对于人物来说，这个参数代表他现在需要主攻的对象是谁
	 */
	private LivingThing attackObject;

	private ConcurrentHashMap<Integer, RoleBuff> buffsMap;

	public LivingThing(int id, int uniqueId, String name) {
		this.id = id;
		this.uniqueId = uniqueId;
		this.name = name;
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

	public void addRoleSkill(RoleSkill roleSkill) {
		skillMap.put(roleSkill.getSkillId(), roleSkill);
	}

	public RoleSkill getRoleSkill(int roleSkillId) {
		return skillMap.get(roleSkillId);
	}

	public void setAttributeMap(Map<Integer, Attribute> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public Map<Integer, Attribute> getAttributeMap() {
		return attributeMap;
	}

	public String getAttributeStr() {
		StringBuilder stringBuilder = new StringBuilder();

		for (Attribute attribute : attributeMap.values()) {
			stringBuilder.append(attribute.getId()).append(":").append(attribute.getValue());
			if (stringBuilder.length() > 0) {
				stringBuilder.append(",");
			}
		}

		return stringBuilder.toString();
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

	public int getSceneId() {
		return sceneId;
	}

	public String getName() {
		return name;
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public LivingThing getAttackObject() {
		return attackObject;
	}

	public void setAttackObject(LivingThing attackObject) {
		this.attackObject = attackObject;
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
				if (buff.isBuff()) {
					effectAttribute(buff.getObjectId(), entry.getKey(), entry.getValue(), buff.getName() + "buff作用");
				} else {
					effectAttribute(buff.getObjectId(), entry.getKey(), -entry.getValue(), buff.getName() + "buff作用");
				}

				buff.setLastUsedTime(now);
			}
		}

		// buff需要移除的时候，判断是否需要回复buff之前的状态
		for (RoleBuff roleBuff : needRemoveBuff) {
			if (roleBuff.isResore()) {
				for (Entry<Integer, Integer> entry : roleBuff.getEffectValue().entrySet()) {
					if (!roleBuff.isBuff()) {
						effectAttribute(0, entry.getKey(), -entry.getValue(), roleBuff.getName() + "buff移除");
					} else {
						effectAttribute(0, entry.getKey(), entry.getValue(), roleBuff.getName() + "buff作用");
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

	/**
	 * 第一个参数确定伤害来源
	 * 
	 * @param attackId
	 * @param key
	 * @param value
	 * @param reason
	 * @return
	 */
	public int effectAttribute(int attackId, int key, int value, String reason) {
		Attribute attribute = attributeMap.get(key);

		// 加血加蓝限制
		if (value > 0) {
			if (attribute.getId() == AttributeEnum.HP.getId()) {
				if (attribute.getValue() + value >= attributeMap.get(AttributeEnum.MAX_HP.getId()).getValue()) {
					value = attributeMap.get(AttributeEnum.MAX_HP.getId()).getValue() - attribute.getValue();
				}
			}
			if (attribute.getId() == AttributeEnum.MP.getId()) {
				if (attribute.getValue() + value >= attributeMap.get(AttributeEnum.MAX_MP.getId()).getValue()) {
					value = attributeMap.get(AttributeEnum.MAX_MP.getId()).getValue() - attribute.getValue();
				}
			}
		}

		int oldValue = attribute.getValue();

		int newValue = attribute.changeValue(value);
		if (isDead()) {
			// 如果死了，定义最后一击的人
			afterDead();
		}

		if (oldValue != newValue) {
			notifyAttributeChange(attribute, reason);
		}

		if (attribute.getId() == AttributeEnum.MAX_MP.getId()
				&& attributeMap.get(AttributeEnum.MP.getId()).getValue() == attribute.getValue()) {
			effectAttribute(attackId, AttributeEnum.MP.getId(), newValue, "最大mp改变");
		}

		if (attribute.getId() == AttributeEnum.MAX_HP.getId()
				&& attributeMap.get(AttributeEnum.HP.getId()).getValue() == attribute.getValue()) {
			effectAttribute(attackId, AttributeEnum.HP.getId(), newValue, "最大mp改变");
		}

		return newValue;
	}

	public boolean isContainBuff(int buffId) {
		return buffsMap.containsKey(buffId);
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
