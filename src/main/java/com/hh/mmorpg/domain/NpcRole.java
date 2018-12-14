package com.hh.mmorpg.domain;

/*
 * npc
 */

public class NpcRole {

	private int id;
	private String name;
	private String talk;

	public NpcRole(int id, String name, String talk) {
		this.id = id;
		this.name = name;
		this.talk = talk;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTalk() {
		return talk;
	}

	@Override
	public String toString() {
		return "Npc [id=" + id + ", name=" + name + "]";
	}

}
