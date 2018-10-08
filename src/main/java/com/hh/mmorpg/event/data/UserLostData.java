package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventData;

public class UserLostData extends EventData {

	private User user;

	public UserLostData(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}
