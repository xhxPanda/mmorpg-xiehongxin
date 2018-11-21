package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class NpcTalkData extends EventData {

	private Role role;
	private int npcId;

	public NpcTalkData(Role role, int npcId) {
		this.role = role;
		this.npcId = npcId;
	}

	public Role getRole() {
		return role;
	}

	public int getNpcId() {
		return npcId;
	}

}
