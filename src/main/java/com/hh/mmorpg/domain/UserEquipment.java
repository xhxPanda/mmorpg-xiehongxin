package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;

public class UserEquipment extends BagMaterial {

	private boolean inUsed;
	private int maxDurability;
	private int durability;
	private int equimentType;
	private Map<Integer, Integer> attributeMap;
	private long gainTime;

	public UserEquipment(int roleId, String name, int id, int maxDurability, int durability, String attributes,
			long gainTime, String sellPrice, int index, int equimentType) {
		super(roleId, name, id, MaterialType.EQUIPMENT_TYPE.getId(), MaterialType.EQUIPMENT_TYPE.getName(), 1, index,
				sellPrice);
		this.inUsed = false;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.attributeMap = new HashMap<>();
		for (String str : attributes.split(",")) {
			String s[] = str.split(":");
			attributeMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}

		this.gainTime = gainTime;
		this.equimentType = equimentType;
	}

	public UserEquipment(int roleId, String name, int id, boolean inUsed, int maxDurability, int durability,
			String attributes, long gainTime, String sellPrice, int index, int equimentType) {
		super(roleId, name, id, MaterialType.EQUIPMENT_TYPE.getId(), MaterialType.EQUIPMENT_TYPE.getName(), 1, index,
				sellPrice);
		this.inUsed = inUsed;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.attributeMap = new HashMap<>();
		for (String str : attributes.split(",")) {
			String s[] = str.split(":");
			attributeMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}

		this.gainTime = gainTime;
		this.equimentType = equimentType;
	}

	public boolean isInUsed() {
		return inUsed;
	}

	public void setInUsed(boolean inUsed) {
		this.inUsed = inUsed;
	}

	public int getMaxDurability() {
		return maxDurability;
	}

	public int getDurability() {
		return durability;
	}

	public Map<Integer, Integer> getAttributeMap() {
		return attributeMap;
	}

	public int getEquimentType() {
		return equimentType;
	}

	public ReplyDomain dropDurability() {
		if (this.durability - 1 > 0) {
			return ReplyDomain.SUCCESS;
		} else {
			this.durability = 0;
			return ReplyDomain.FAILE;
		}

	}

	public long getGainTime() {
		return gainTime;
	}

	public String getAttributeStr() {
		StringBuilder builder = new StringBuilder();

		for (Entry<Integer, Integer> entry : attributeMap.entrySet()) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(entry.getKey()).append(":").append(entry.getValue());
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "UserEquiment [name=" + getName() + ", id=" + getId() + ", 最大耐久度=" + maxDurability + ", 耐久度="
				+ durability + "]";
	}

	public static final ResultBuilder<UserEquipment> BUILDER = new ResultBuilder<UserEquipment>() {

		@Override
		public UserEquipment build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt(1);
			int id = result.getInt(2);
			String name = result.getString(3);
			long gainTime = result.getLong(4);
			String attributeStr = result.getString(5);
			int maxDurability = result.getInt(6);
			int durability = result.getInt(7);
			String sellPrice = result.getString("sellPrice");
			boolean inUsed = result.getBoolean("isInUsed");
			int index = result.getInt("index");
			int type = result.getInt("type");
			return new UserEquipment(roleId, name, id, inUsed, maxDurability, durability, attributeStr, gainTime,
					sellPrice, index, type);
		}
	};
}
