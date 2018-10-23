package com.hh.mmorpg.domain;

import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;

public class Monster extends LivingThing {

	private String name;
	private int freshTime;
	private int sceneId;

	public Monster(int id, int uniqueId, String name, int freshTime, int sceneId) {
		super(id, uniqueId);
		this.name = name;
		this.freshTime = freshTime;
		this.sceneId = sceneId;
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

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub
		setStatus(false);
		setBeKilledTime(System.currentTimeMillis());

		MonsterDeadData data = new MonsterDeadData(getId(), sceneId);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.MONSTER_DEAD, new EventDealData<MonsterDeadData>(data));
	}

	public int getSceneId() {
		return sceneId;
	}

}
