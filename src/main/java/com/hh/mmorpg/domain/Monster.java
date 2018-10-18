package com.hh.mmorpg.domain;

public class Monster extends LivingThing {

	private String name;
	private int freshTime;

	public Monster(int id, int uniqueId, String name, int freshTime) {
		super(id, uniqueId);
		this.name = name;
		this.freshTime = freshTime;
	}

	public int getFreshTime() {
		return freshTime;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Monster [name=" + name + ", freshTime=" + freshTime +", id=" + getUniqueId() + ", hp=" + getAttribute(3).getValue() + ", mp=" + getAttribute(4).getValue() + " ]";
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub
		
		setStatus(false);
		setBeKilledTime(System.currentTimeMillis());
	}

}
