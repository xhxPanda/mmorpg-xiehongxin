package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class MissionCompeteData extends EventData {

	private int mission;
	private Role role;

	public MissionCompeteData(int mission, Role role) {
		this.mission = mission;
		this.role = role;
	}

	public int getMission() {
		return mission;
	}

	public Role getRole() {
		return role;
	}

}
