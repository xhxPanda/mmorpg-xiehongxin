package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

public class MonsterDeadData extends EventData {

	private Monster monster;
	private Role killRole;
	private int sceneId;

	public MonsterDeadData(Monster monster, Role killRole, int sceneId) {
		this.monster = monster;
		this.killRole = killRole;
		this.sceneId = sceneId;
	}

	public Monster getMonster() {
		return monster;
	}

	public Role getKillRole() {
		return killRole;
	}

	public int getSceneId() {
		return sceneId;
	}

}
