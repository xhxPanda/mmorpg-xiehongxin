package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class GuildJoinData extends EventData {

	private Role role;
	private int guildId;

	public GuildJoinData(Role role, int guildId) {
		this.role = role;
		this.guildId = guildId;
	}

	public Role getRole() {
		return role;
	}

	public int getGuildId() {
		return guildId;
	}

}
