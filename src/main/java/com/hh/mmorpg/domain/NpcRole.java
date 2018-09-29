package com.hh.mmorpg.domain;

public class NpcRole {

	private int id;
	private String name;

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

}
