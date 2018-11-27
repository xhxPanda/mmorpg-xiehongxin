package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class FriendData extends EventData {

	private Role role;
	private int applyRoleId;

	public FriendData(Role role, int applyRoleId) {
		this.role = role;
		this.applyRoleId = applyRoleId;
	}

	public Role getRole() {
		return role;
	}

	public int getApplyRoleId() {
		return applyRoleId;
	}

}
