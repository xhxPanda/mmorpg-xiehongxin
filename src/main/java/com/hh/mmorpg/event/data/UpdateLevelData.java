package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class UpdateLevelData extends EventData {

	private int oldLevel;
	private int newLevel;
	private Role role;

	public UpdateLevelData(int oldLevel, int newLevel, Role role) {
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
		this.role = role;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}

	public Role getRole() {
		return role;
	}

}
