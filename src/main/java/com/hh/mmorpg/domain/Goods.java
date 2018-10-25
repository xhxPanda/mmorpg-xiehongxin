package com.hh.mmorpg.domain;

public class Goods {

	private int id;
	private String name;
	private String item;
	private String price;

	public Goods(int id, String name, String item, String price) {
		this.id = id;
		this.name = name;
		this.item = item;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getItem() {
		return item;
	}

	public String getPrice() {
		return price;
	}

}
