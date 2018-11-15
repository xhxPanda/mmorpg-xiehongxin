package com.hh.mmorpg.domain;

public enum GuildMemberIdentity {

	NORMAL_MEMBER(1, "普通成员"), 
	ELITES_MEMBER(2, "精英成员"), 
	VICE_PRESIDENT(3, "副会长"), 
	PRESIDENT(4, "会长");

	private int id;
	private String name;

	private GuildMemberIdentity(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
