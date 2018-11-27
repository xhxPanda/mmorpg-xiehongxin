package com.hh.mmorpg.event.data;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.TeamMate;
import com.hh.mmorpg.event.EventData;

public class JoinTeamData extends EventData {

	private Role role;
	private List<TeamMate> teamMate;

	public JoinTeamData(Role role, List<TeamMate> teamMate) {
		this.role = role;
		this.teamMate = teamMate;
	}

	public Role getRole() {
		return role;
	}

	public List<TeamMate> getTeamMate() {
		return teamMate;
	}

}
