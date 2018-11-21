package com.hh.mmorpg.domain;

import java.util.HashMap;
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
			Map<String, Integer> missionAtts) {
		this.missionId = missionId;
		this.roleId = roleId;
		this.status = status;
		this.name = name;
		this.dec = dec;
		this.type = type;

		this.attMap = new HashMap<>();
		for (Entry<String, Integer> entry : missionAtts.entrySet()) {
			MissionAttribute missionAttribute = new MissionAttribute(entry.getKey(), 0, entry.getValue());
			attMap.put(missionAttribute.getDec(), missionAttribute);
		}
	}

	/**
	 * 判断该任务是否完成
	 * 
	 * @return
	 */
	public boolean isMissionConpete() {
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

	class MissionAttribute {
		private String dec;
		private int pocess;
		private int max;

		public MissionAttribute(String dec, int pocess, int max) {
			this.dec = dec;
			this.pocess = pocess;
			this.max = max;
		}

		public String getDec() {
			return dec;
		}

		public int getPocess() {
			return pocess;
		}

		public int getMax() {
			return max;
		}

		public void changeProcess(int value) {
			this.pocess = (pocess + value) >= max ? max : (pocess + value);
		}

	}
}
