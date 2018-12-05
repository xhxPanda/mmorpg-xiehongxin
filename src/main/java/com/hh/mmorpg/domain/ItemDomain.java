package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDomain {

	private int id;
	private String name;
	private Map<Integer, Integer> effectAttribuate;
	private List<Integer> buffs;
	private long cd;
	private String sellPrice;
	private int pileNum;

	public ItemDomain(int id, String name, String effectAttribuate, String buffs, long cd, String sellPrice,
			int pileNum) {
		this.id = id;
		this.name = name;

		this.cd = cd;
		this.sellPrice = sellPrice;
		this.pileNum = pileNum;

		this.effectAttribuate = new HashMap<>();
		for (String str : effectAttribuate.split(",")) {
			String s[] = str.split(":");
			this.effectAttribuate.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));

		}

		this.buffs = new ArrayList<Integer>();
		if (!buffs.isEmpty()) {
			for (String buff : buffs.split(",")) {
				this.buffs.add(Integer.parseInt(buff));
			}
		}

	}

	public int getId() {
		return id;
	}

	public Map<Integer, Integer> getEffectAttribuate() {
		return effectAttribuate;
	}

	public List<Integer> getBuffs() {
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
