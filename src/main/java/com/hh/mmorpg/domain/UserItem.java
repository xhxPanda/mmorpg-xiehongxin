package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class UserItem extends Material {

	private long lastUsedTime;
	private Map<Integer, Integer> effectAttributeMap;
	private List<Integer> buffList;
	private long cd;
	private long gainTime;

	public UserItem(int roleId, String name, int id, int quantity, long gainTime, long lastUsedTime, String effect,
			String buffs, long cd) {
		// TODO Auto-generated constructor stub
		super(roleId, name, id, MaterialType.ITEM_TYPE_ID, quantity);
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

	public boolean isPartCD() {
		return (System.currentTimeMillis() - lastUsedTime) > cd;
	}

	public long getGainTime() {
		return gainTime;
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
			long lastUserdTime = result.getLong("lastUserdTime");
			long cd = result.getLong("cd");
			String effects = result.getString("effectAttribute");
			String buffs = result.getString("buffs");
			return new UserItem(roleId, name, id, quantity, gainTime, lastUserdTime, effects, buffs, cd);
		}
	};

}
