package com.hh.mmorpg.server.domain;

public class ItemDomain {

	private int id;
	private String name;
	private String effectAttribuate;
	private String buffs;
	private long cd;

	public ItemDomain(int id, String name, String effectAttribuate, String buffs, long cd) {
		this.id = id;
		this.name = name;
		this.effectAttribuate = effectAttribuate;
		this.buffs = buffs;
		this.cd = cd;
	}

	public int getId() {
		return id;
	}

	public String getEffectAttribuate() {
		return effectAttribuate;
	}

	public String getBuffs() {
		return buffs;
	}

	public String getName() {
		return name;
	}

	public long getCd() {
		return cd;
	}

}
