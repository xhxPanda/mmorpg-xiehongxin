package com.hh.mmorpg.domain;

import java.util.Map;

import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;

public class Monster extends LivingThing {

	private String name;
	private int freshTime;
	private int sceneId;
	private int attackRoleId;

	private Map<String, Integer> killFallItemMap;

	public Monster(int uniqueId, int sceneId, MonsterDomain domain) {
		super(domain.getId(), uniqueId);
		this.name = domain.getName();
		this.freshTime = domain.getFreshTime();
		this.sceneId = sceneId;
		this.killFallItemMap = domain.getKillFallItemMap();
		setAttributeMap(domain.getAttributeMap());
		setSkillMap(domain.getRoleSkillMap());
	}

	public int getFreshTime() {
		return freshTime;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Monster [name=" + name + ", freshTime=" + freshTime + ", id=" + getUniqueId() + ", hp="
				+ getAttribute(3).getValue() + ", mp=" + getAttribute(4).getValue() + " ]";
	}

	public synchronized void setAttackRole(int roleId) {
		if (attackRoleId == 0) {
			this.attackRoleId = roleId;
		}
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub
		setStatus(false);
		setBeKilledTime(System.currentTimeMillis());

		MonsterDeadData data = new MonsterDeadData(getId(), attackRoleId, getSceneId());
		EventHandlerManager.INSATNCE.methodInvoke(EventType.MONSTER_DEAD, new EventDealData<MonsterDeadData>(data));
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public int getSceneId() {
		return sceneId;
	}

	public int getAttackRoleId() {
		return attackRoleId;
	}

	public Map<String, Integer> getKillFallItemMap() {
		return killFallItemMap;
	}

	public void setKillFallItemMap(Map<String, Integer> killFallItemMap) {
		this.killFallItemMap = killFallItemMap;
	}

}
