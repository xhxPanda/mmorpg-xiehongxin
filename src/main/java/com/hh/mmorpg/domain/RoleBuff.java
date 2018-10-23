package com.hh.mmorpg.domain;

import java.util.Map;

public class RoleBuff {

	private long effectTime; // 生效时间
	private long lastUsedTime; // 上一次使用时间
	private int buffId;
	private boolean buff; // 是否有增益效果
	private boolean resore; // 是否可以恢复
	private long lastTime; // 持续时间
	private long heartbeatTime; // 每多少时间作用一次
	private Map<Integer, Integer> effectValue;

	public RoleBuff(int buffId, long effectTime, Map<Integer, Integer> effectValue, boolean buff,
			boolean resore, long lastTime, long heartbeatTime) {
		this.lastUsedTime = 0;
		this.buffId = buffId;
		this.effectValue = effectValue;
		this.effectTime = effectTime;
		this.buff = buff;
		this.resore = resore;
		this.lastTime = lastTime;
		this.heartbeatTime = heartbeatTime;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public int getBuffId() {
		return buffId;
	}

	public long getEffectTime() {
		return effectTime;
	}

	public boolean isBuff() {
		return buff;
	}

	public long getLastTime() {
		return lastTime;
	}

	public Map<Integer, Integer> getEffectValue() {
		return effectValue;
	}

	public boolean isResore() {
		return resore;
	}

	public long getHeartbeatTime() {
		return heartbeatTime;
	}

	public boolean isExpire(long now) {
		if (lastTime == -1) {
			return false;
		}
		return (now - effectTime) >= lastTime;
	}

	@Override
	public String toString() {
		return "RoleBuff [effectTime=" + effectTime + ", lastUsedTime=" + lastUsedTime + ", buffId=" + buffId
				+ ", buff=" + buff + ", resore=" + resore + ", lastTime=" + lastTime + ", heartbeatTime="
				+ heartbeatTime + ", effectValue=" + effectValue + "]";
	}

}
