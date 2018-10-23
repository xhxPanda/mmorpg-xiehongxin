package com.hh.mmorpg.domain;

/**
 * type为1是人，type为2是怪物
 */

public class BattleObject {

	private int id;
	private int type;
	private boolean target;

	public BattleObject(int id, int type, boolean target) {
		this.id = id;
		this.type = type;
		this.target = target;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}

}
