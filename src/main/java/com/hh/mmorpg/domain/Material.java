package com.hh.mmorpg.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Material {

	private int roleId;
	private String name;
	private int id;
	private int type;
	private String typeName;
	private AtomicInteger quantity;
	private String sellPrice;

	public Material(int roleId, String name, int id, int type, String typeName, int quantity) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.typeName = typeName;
		this.quantity = new AtomicInteger(quantity);
		this.roleId = roleId;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}


	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity.get();
	}

	public int getRoleId() {
		return roleId;
	}

	public int changeQuantity(int num) {
		return quantity.addAndGet(num);
	}

	public String getSellPrice() {
		return sellPrice;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setSellPrice(String sellPrice) {
		this.sellPrice = sellPrice;
	}

	@Override
	public String toString() {
		return "Material [ 名称=" + name + ", id=" + id + ", 类型=" + typeName + ", 数量="
				+ quantity + "]";
	}

}
