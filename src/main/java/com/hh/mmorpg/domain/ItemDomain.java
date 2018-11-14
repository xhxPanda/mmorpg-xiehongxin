package com.hh.mmorpg.domain;

public class ItemDomain {

	private int id;
	private String name;
	private String effectAttribuate;
	private String buffs;
	private long cd;
	private String sellPrice;
	private int pileNum;

	public ItemDomain(int id, String name, String effectAttribuate, String buffs, long cd, String sellPrice, int pileNum) {
		this.id = id;
		this.name = name;
		this.effectAttribuate = effectAttribuate;
		this.buffs = buffs;
		this.cd = cd;
		this.sellPrice = sellPrice;
		this.pileNum = pileNum;
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

	public String getSellPrice() {
		return sellPrice;
	}

	public int getPileNum() {
		return pileNum;
	}

}
