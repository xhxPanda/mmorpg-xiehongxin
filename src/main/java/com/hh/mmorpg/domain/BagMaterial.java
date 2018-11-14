package com.hh.mmorpg.domain;

/**
 * 背包物品基类
 * 
 * @author 37
 *
 */
public class BagMaterial extends Material {

	private int index;
	private String sellPrice;
	private int quantity;

	public BagMaterial(int roleId, String name, int id, int type, String typeName, int quantity, int index,
			String sellPrice) {
		super(roleId, name, id, type, typeName);
		// TODO Auto-generated constructor stub

		this.index = index;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
	}

	public int getIndex() {
		return index;
	}

	public String getSellPrice() {
		return sellPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int changeQuantity(int value) {
		return quantity += value;
	}
}
