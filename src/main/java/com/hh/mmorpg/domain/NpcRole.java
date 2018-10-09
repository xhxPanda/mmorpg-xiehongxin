package com.hh.mmorpg.domain;

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
		return "NpcRole [id=" + id + ", name=" + name + "]";
	}

}
