package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用户所接受的任务
 * 
 * @author 37
 *
 */
public class RoleMission {

	private int missionId;
	private int roleId;
	private String name;
	private String dec;
	private int type;
	private int status;
	private Map<String, MissionAttribute> attMap;

	/**
	 * 用户新接任务
	 * 
	 * @param missionId
	 * @param roleId
	 * @param status
	 * @param missionAtts
	 */
	public RoleMission(int missionId, int roleId, String name, String dec, int type, int status,
			Map<String, MissionAttribute> missionAtts) {
		this.missionId = missionId;
		this.roleId = roleId;
		this.status = status;
		this.name = name;
		this.dec = dec;
		this.type = type;

		this.attMap = missionAtts;
	}

	/**
	 * 判断该任务是否完成
	 * 
	 * @return
	 */
	public boolean isMissionCompete() {
		for (MissionAttribute missionAttribute : attMap.values()) {
			if (missionAttribute.getPocess() < missionAttribute.getMax()) {
				return false;
			}
		}
		return true;
	}

	public void updateMissionProcess(String key, int process) {
		attMap.get(key).changeProcess(process);
	}

	public int getMissionId() {
		return missionId;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}

	public String getDec() {
		return dec;
	}

	public int getType() {
		return type;
	}

	public boolean isMissionContainKey(String key) {
		return attMap.containsKey(key);
	}

	public List<String> getKeys() {
		return new ArrayList<>(attMap.keySet());
	}
}
