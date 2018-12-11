package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 用户所接受的任务
 * 
 * @author xhx
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

	public RoleMission(int missionId, int roleId, String name, String dec, int type, int status, String attrs) {
		this.missionId = missionId;
		this.roleId = roleId;
		this.name = name;
		this.dec = dec;
		this.type = type;
		this.status = status;

		this.attMap = new HashMap<>();
		for (String ss : attrs.split(",")) {
			String str[] = ss.split(":");
			attMap.put(str[0], new MissionAttribute(str[0], str[1], Integer.parseInt(str[3]), Integer.parseInt(str[2])));
		}
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

	public void setStatus(int status) {
		this.status = status;
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

	public Map<String, MissionAttribute> getAttMap() {
		return attMap;
	}

	public MissionAttribute getAtt(String key) {
		return attMap.get(key);
	}

	public List<String> getKeys() {
		return new ArrayList<>(attMap.keySet());
	}

	public String getDbAttr() {
		StringBuilder stringBuilder = new StringBuilder();

		for (MissionAttribute attribute : attMap.values()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(attribute.dbString());
		}

		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		return "任务 [任务id=" + missionId + ", 名称=" + name + ", 描述=" + dec + ", status=" + (status == -1 ? "待完成" : "已完成")
				+ "," + getMissionProcess() + "]";
	}

	private String getMissionProcess() {
		StringBuilder stringBuilder = new StringBuilder();
		for (MissionAttribute missionAttribute : attMap.values()) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(",");
			}

			stringBuilder.append(missionAttribute.toString());
		}

		return stringBuilder.toString();
	}

	public static final ResultBuilder<RoleMission> BUILDER = new ResultBuilder<RoleMission>() {

		@Override
		public RoleMission build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int missionId = result.getInt("missionId");
			int roleId = result.getInt("roleId");
			int type = result.getInt("type");
			int status = result.getInt("status");

			String dec = result.getString("dec");
			String attrs = result.getString("process");
			String name = result.getString("name");
			return new RoleMission(missionId, roleId, name, dec, type, status, attrs);
		}
	};

}
