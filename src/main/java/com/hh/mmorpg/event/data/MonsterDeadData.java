package com.hh.mmorpg.event.data;

import com.hh.mmorpg.event.EventData;

public class MonsterDeadData extends EventData {

	private int monsterId;
	private int killRoleId;
	private int sceneId;

	public MonsterDeadData(int monsterId, int killRoleId, int sceneId) {
		this.monsterId = monsterId;
		this.killRoleId = killRoleId;
		this.sceneId = sceneId;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public int getKillRoleId() {
		return killRoleId;
	}

	public int getSceneId() {
		return sceneId;
	}

}
