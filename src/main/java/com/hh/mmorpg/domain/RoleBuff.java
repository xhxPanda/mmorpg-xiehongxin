package com.hh.mmorpg.domain;

import java.util.Map;

public class RoleBuff {

	private long effectTime;
	private long lastUsedTime;
	private int buffId;
	private boolean buff;
	private boolean resore;
	private long lastTime;
	private long heartbeatTime;
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
