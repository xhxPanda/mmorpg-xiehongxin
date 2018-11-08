package com.hh.mmorpg.domain;

/*
 * npc
 */

public class NpcRole {

	int id;
	String name;

	public NpcRole(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "npc角色:[name=" + name + "]";
	}

}
