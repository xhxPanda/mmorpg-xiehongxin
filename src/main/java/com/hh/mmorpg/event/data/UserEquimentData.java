package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class UserEquimentData extends EventData {
	
	private Role role;
	
	public UserEquimentData(Role role) {
		this.role = role;
	}

	public Role getRole() {
		return role;
	}
	
	
}
