package com.hh.mmorpg.event.data;

import com.hh.mmorpg.event.EventData;

public class PKData extends EventData {

	private int winRoleId;
	private int loseRoleId;

	public PKData(int winRoleId, int loseRoleId) {
		this.winRoleId = winRoleId;
		this.loseRoleId = loseRoleId;
	}

	public int getWinRoleId() {
		return winRoleId;
	}

	public int getLoseRoleId() {
		return loseRoleId;
	}

}
