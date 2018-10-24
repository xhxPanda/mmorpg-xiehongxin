package com.hh.mmorpg.domain;

public class Goods {

	private int id;
	private String material;
	private String price;

	public Goods(int id, String material, String price) {
		this.id = id;
		this.material = material;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public String getMaterial() {
		return material;
	}

	public String getPrice() {
		return price;
	}

}
