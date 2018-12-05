package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class RoleChangeData extends EventData {

	private int userId;
	private Role oldRole;
	private Role newRole;

	public RoleChangeData(int userId, Role oldRole, Role newRole) {
		this.userId = userId;
		this.oldRole = oldRole;
		this.newRole = newRole;
	}

	public int getUserId() {
		return userId;
	}

	public Role getOldRole() {
		return oldRole;
	}

	public Role getNewRole() {
		return newRole;
	}

}
