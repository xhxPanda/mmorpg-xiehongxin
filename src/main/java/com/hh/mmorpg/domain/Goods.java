package com.hh.mmorpg.domain;

public class Goods {

	private int id;
	private String name;
	private String item;
	private String price;
	private String priceName;

	public Goods(int id, String name, String item, String price, String priceName) {
		this.id = id;
		this.name = name;
		this.item = item;
		this.price = price;
		this.priceName = priceName;
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

	@Override
	public String toString() {
		return "goods [id为" + id + ", 名称：" + name + " 价格：" + priceName + "]";
	}

}
