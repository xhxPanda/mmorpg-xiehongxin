package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;

public class UserEquipment {

	private int roleId;
	private int uniqueId;
	private int materialId; // 模版id
	private String name; // 名称
	private String sellPrice; // 销售价格
	private int equimentLevel; // 武器等级
	private int equimentSource; // 武器评分
	private int maxDurability;
	private int durability;
	private int equimentType;
	private boolean inUsed;
	private Map<Integer, Integer> attributeMap;

	public UserEquipment(int roleId, int uniqueId, int materialId, String name, String sellPrice, int equimentLevel,
			int equimentSource, int maxDurability, int durability, int equimentType,
			String attributes, boolean inUsed) {
		this.roleId = roleId;
		this.uniqueId = uniqueId;
		this.materialId = materialId;
		this.name = name;
		this.sellPrice = sellPrice;
		this.equimentLevel = equimentLevel;
		this.equimentSource = equimentSource;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.equimentType = equimentType;

		this.attributeMap = new HashMap<>();
		for (String str : attributes.split(",")) {
			String s[] = str.split(":");
			attributeMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}
		this.inUsed = inUsed;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getMaterialId() {
		return materialId;
	}

	public String getName() {
		return name;
	}

	public String getSellPrice() {
		return sellPrice;
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

	public int getEquimentLevel() {
		return equimentLevel;
	}

	public int getEquimentSource() {
		return equimentSource;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public static final ResultBuilder<UserEquipment> BUILDER = new ResultBuilder<UserEquipment>() {

		@Override
		public UserEquipment build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub

			int roleId = result.getInt("roleId");
			int equimentId = result.getInt("equimentId");
			int uniqueId = result.getInt("uniqueId");
			String name = result.getString("name");
			String sellPrice = result.getString("sellPrice");
			int equimentLevel = result.getInt("equimentLevel");
			int equimentSource = result.getInt("equimentSource");
			String attributes = result.getString("effectAttribute");
			int maxDurability = result.getInt("maxDurability");
			int durability = result.getInt("durability");
			int equimentType = result.getInt("equimentType");
			boolean inUsed = result.getBoolean("isInUsed");

			return new UserEquipment(roleId, uniqueId, equimentId, name, sellPrice, equimentLevel, equimentSource,
					maxDurability, durability, equimentType, attributes, inUsed);
		}
	};
}
