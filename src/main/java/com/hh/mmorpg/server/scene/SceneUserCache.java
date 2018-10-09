package com.hh.mmorpg.server.scene;

import com.hh.mmorpg.domain.Role;

public class SceneUserCache {

	private int userId;
	private Role role;

	public SceneUserCache(int userId, Role role) {
		this.userId = userId;
		this.role = role;
	}

	public int getUserId() {
		return userId;
	}

	public Role getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "SceneUserCache [userId=" + userId + ", role=" + role.toString() + "]";
	}

}
