package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class TransactionData extends EventData {

	private Role role;
	private Role anotherRole;

	public TransactionData(Role role, Role anotherRole) {
		this.role = role;
		this.anotherRole = anotherRole;
	}

	public Role getRole() {
		return role;
	}

	public Role getAnotherRole() {
		return anotherRole;
	}

}
