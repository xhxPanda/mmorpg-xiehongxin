package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserItem extends Material {

	private long lastUsedTime;
	private Map<Integer, Integer> effectAttributeMap;
	private List<Integer> buffList;
	private long cd;
	private long gainTime;
	private String sellPrice;

	public UserItem(int roleId, String name, int id, int quantity, long gainTime, long lastUsedTime, String effect,
			String buffs, long cd, String sellPrice) {
		// TODO Auto-generated constructor stub
		super(roleId, name, id, MaterialType.ITEM_TYPE.getId(), MaterialType.ITEM_TYPE.getName(), quantity);
		this.lastUsedTime = lastUsedTime;

		this.effectAttributeMap = new HashMap<>();
		for (String str : effect.split(",")) {
			String s[] = str.split(":");
			effectAttributeMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}

		this.buffList = new ArrayList<>();
		if (buffs != null && !buffs.isEmpty()) {
			for (String str : buffs.split(",")) {

				buffList.add(Integer.parseInt(str));
			}
		}

		this.cd = cd;
		this.gainTime = gainTime;
		this.sellPrice = sellPrice;
	}

	public Map<Integer, Integer> getEffectAttributeMap() {
		return effectAttributeMap;
	}

	public List<Integer> getBuffList() {
		return buffList;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public long getCd() {
		return cd;
	}

	public String getSellPrice() {
		return sellPrice;
	}

	public boolean isPartCD() {
		return (System.currentTimeMillis() - lastUsedTime) > cd;
	}

	public long getGainTime() {
		return gainTime;
	}

	public String getAttributeStr() {
		StringBuilder builder = new StringBuilder();

		for (Entry<Integer, Integer> entry : effectAttributeMap.entrySet()) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(entry.getKey()).append(":").append(entry.getValue());
		}

		return builder.toString();
	}

	public String getBuffStr() {
		StringBuilder builder = new StringBuilder();
		for (Integer i : buffList) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(i);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return "UserItem [ name=" + getName() + ", 数量=" + getQuantity() + "]";
	}

	public static final ResultBuilder<UserItem> BUILDER = new ResultBuilder<UserItem>() {

		@Override
		public UserItem build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int roleId = result.getInt("roleId");
			int id = result.getInt("itemId");
			String name = result.getString("name");
			int quantity = result.getInt("quantity");
			long gainTime = result.getLong("gainTime");
			long lastUserdTime = result.getLong("lastUsedTime");
			long cd = result.getLong("cd");
			String effects = result.getString("effectAttribute");
			String buffs = result.getString("buffs");
			String sellPrice = result.getString("sellPrice");
			return new UserItem(roleId, name, id, quantity, gainTime, lastUserdTime, effects, buffs, cd, sellPrice);
		}
	};

}
