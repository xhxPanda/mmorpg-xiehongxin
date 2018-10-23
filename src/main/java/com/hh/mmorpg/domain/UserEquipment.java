package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserEquipment extends Material {

	private String name;
	private int clothesId;
	private boolean inUsed;
	private int maxDurability;
	private int durability;
	private Map<Integer, Integer> attributeMap;

	public UserEquipment(int roleId, String name, int clothesId, int maxDurability, int durability, String attributes,
			long gainTime) {
		super(roleId, name, clothesId, MaterialType.CLOTHES_TYPE_ID, 1, gainTime);
		this.inUsed = false;
		this.maxDurability = maxDurability;
		this.durability = durability;
		this.attributeMap = new HashMap<>();
		for (String str : attributes.split(",")) {
			String s[] = str.split(":");
			attributeMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}
	}

	public int getClothesId() {
		return clothesId;
	}

	public boolean isInUsed() {
		return inUsed;
	}

	public void setInUsed(boolean inUsed) {
		this.inUsed = inUsed;
	}

	public String getName() {
		return name;
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
	
	public void dropDurability() {
		this.durability -= 1;
	}

	@Override
	public String toString() {
		return "UserClothes [name=" + name + ", clothesId=" + clothesId + ", maxDurability="
				+ maxDurability + ", durability=" + durability + "]";
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
			return new UserEquipment(roleId, name, id, maxDurability, durability, attributeStr, gainTime);
		}
	};
}
