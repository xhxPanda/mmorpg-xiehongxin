package com.hh.mmorpg.domain;

/**
 * 
 * @author xhx 怪物死亡后的掉落物品实体
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
		return "MonsterBeKillBonus [id=" + id + ", 角色id=" + roleId + ", monsterId=" + monsterId + ", 生成时间=" + effectTime
				+ ", 奖励=" + bonus + "]";
	}

}
