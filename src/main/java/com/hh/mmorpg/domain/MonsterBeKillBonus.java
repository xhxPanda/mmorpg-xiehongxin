package com.hh.mmorpg.domain;

/**
 * 
 * @author xhx 怪物死亡后的掉落实体
 *
 */

public class MonsterBeKillBonus {

	private int id;
	private int roleId;
	private int monsterId;
	private long effectTime;
	private String bonus;

	public MonsterBeKillBonus(int id, int roleId, int monsterId, long effectTime, String bonus) {
		this.id = id;
		this.roleId = roleId;
		this.monsterId = monsterId;
		this.effectTime = effectTime;
		this.bonus = bonus;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public long getEffectTime() {
		return effectTime;
	}

	public String getBonus() {
		return bonus;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "MonsterBeKillBonus [id=" + id + ", roleId=" + roleId + ", monsterId=" + monsterId + ", effectTime="
				+ effectTime + ", bonus=" + bonus + "]";
	}

}
