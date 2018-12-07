package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class PassCopyData extends EventData {

	private Role role;
	private int sceneId;

	public PassCopyData(Role role, int sceneId) {
		this.role = role;
		this.sceneId = sceneId;
	}

	public Role getRole() {
		return role;
	}

	public int getSceneId() {
		return sceneId;
	}

}
