package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class GainTreasureData extends EventData {

	private Role role;
	private int id;
	private long gainTreasure;

	public GainTreasureData(Role role, int id, long gainTreasure) {
		this.role = role;
		this.id = id;
		this.gainTreasure = gainTreasure;
	}

	public int getId() {
		return id;
	}

	public Role getRole() {
		return role;
	}

	public long getGainTreasure() {
		return gainTreasure;
	}

}
