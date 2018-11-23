package com.hh.mmorpg.event.data;

import java.util.Set;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class JoinTeamData extends EventData {

	private Role role;
	private Set<Integer> teamMate;

	public JoinTeamData(Role role, Set<Integer> teamMate) {
		this.role = role;
		this.teamMate = teamMate;
	}

	public Role getRole() {
		return role;
	}

	public Set<Integer> getTeamMate() {
		return teamMate;
	}

}
