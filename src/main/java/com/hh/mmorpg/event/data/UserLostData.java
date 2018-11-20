package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventData;

public class UserLostData extends EventData {

	private User user;
	private Role role;

	public UserLostData(User user, Role role) {
		this.user = user;
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public Role getRole() {
		return role;
	}

}
