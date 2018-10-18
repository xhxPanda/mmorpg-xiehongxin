package com.hh.mmorpg.domain;

import java.util.HashMap;
import java.util.Map;

public class BuffDomain {

	private int id;
	private String name;
	private long lastTime;
	private long heartbeatTime;
	private boolean isBuff;
	private boolean isCanResore;
	private Map<Integer, Integer> attributeEffect;

	public BuffDomain(int id, String name, long lastTime, long heartbeatTime, boolean isBuff, String attributeStr,
			boolean isCanResore) {
		this.id = id;
		this.name = name;
		this.lastTime = lastTime;
		this.heartbeatTime = heartbeatTime;
		this.isBuff = isBuff;
		this.isCanResore = isCanResore;

		this.attributeEffect = new HashMap<>();
		for (String str : attributeStr.split(",")) {
			String[] strList = str.split(":");

			attributeEffect.put(Integer.parseInt(strList[0]), Integer.parseInt(strList[1]));
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getLastTime() {
		return lastTime;
	}

	public long getHeartbeatTime() {
		return heartbeatTime;
	}

	public boolean isBuff() {
		return isBuff;
	}

	public boolean isCanResore() {
		return isCanResore;
	}

	public Map<Integer, Integer> getAttributeEffect() {
		return attributeEffect;
	}

}
