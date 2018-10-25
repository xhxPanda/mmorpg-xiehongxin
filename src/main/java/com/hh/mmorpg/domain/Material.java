package com.hh.mmorpg.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class Material {

	private int roleId;
	private String name;
	private int id;
	private int type;
	private AtomicInteger quantity;

	public Material(int roleId, String name, int id, int type, int quantity) {
		this.name = name;
		this.id = id;
		this.type = type;
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

}
