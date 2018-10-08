package com.hh.mmorpg.server.scene;

public class UserScaneDomain {

	private int userId;
	private int type; // 生存还是死亡

	public UserScaneDomain(int userId, int type) {
		super();
		this.userId = userId;
		this.type = type;
	}

	public int getUserId() {
		return userId;
	}

	public int getType() {
		return type;
	}

}
