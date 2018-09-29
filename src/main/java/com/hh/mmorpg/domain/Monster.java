package com.hh.mmorpg.domain;

public class Monster {

	private int id;
	private String name;
	private int flashTime;
	private long beKilledTime;

	public Monster(int id, String name, int flashTime) {
		super();
		this.id = id;
		this.name = name;
		this.flashTime = flashTime;
	}

	public long getBeKilledTime() {
		return beKilledTime;
	}

	public void setBeKilledTime(long beKilledTime) {
		this.beKilledTime = beKilledTime;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getFlashTime() {
		return flashTime;
	}

}
