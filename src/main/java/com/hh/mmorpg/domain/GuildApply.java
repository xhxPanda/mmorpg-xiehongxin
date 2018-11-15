package com.hh.mmorpg.domain;

public class GuildApply {

	private int roleId;
	private int userId;
	private String name;
	private int guildId;
	private String content;

	public GuildApply(int roleId, int userId, String name, int guildId, String content) {
		this.roleId = roleId;
		this.userId = userId;
		this.name = name;
		this.guildId = guildId;
		this.content = content;
	}

	public int getRoleId() {
		return roleId;
	}

	public String getName() {
		return name;
	}

	public int getGuildId() {
		return guildId;
	}

	public String getContent() {
		return content;
	}

	public int getUserId() {
		return userId;
	}

}
