package com.hh.mmorpg.event.data;

import com.hh.mmorpg.event.EventData;

public class MonsterDeadData extends EventData {

	private int id;
	private int sceneId;

	public MonsterDeadData(int id, int sceneId) {
		this.id = id;
		this.sceneId = sceneId;
	}

	public int getId() {
		return id;
	}

	public int getSceneId() {
		return sceneId;
	}

}
