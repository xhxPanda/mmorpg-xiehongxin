package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 背包物品基类
 * 
 * @author 37
 *
 */
public class BagMaterial extends Material {

	private int uniqueId;
	private int index;
	private String sellPrice;
	private int quantity;

	public BagMaterial(BagMaterial bagMaterial, int roleId, int quantity) {
		super(roleId, bagMaterial.getId(), bagMaterial.getName(), bagMaterial.getTypeId());
		this.uniqueId = bagMaterial.uniqueId;
		this.quantity = quantity;
		this.index = 0;
		this.sellPrice = bagMaterial.getSellPrice();
	}

	public BagMaterial(int uniqueId, int roleId, int id, String name,int typeId, int quantity, int index, String sellPrice) {
		super(roleId, id, name, typeId);
		// TODO Auto-generated constructor stub
		this.uniqueId = uniqueId;
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

	public int getUniqueId() {
		return uniqueId;
	}
	
	public static final ResultBuilder<BagMaterial> BUILDER = new ResultBuilder<BagMaterial>() {

		@Override
		public BagMaterial build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			
			int uniqueId = result.getInt("uniqueId");
			int id = result.getInt("id");
			int roleId = result.getInt("roleId");
			int typeId = result.getInt("typeId");
			int quantity = result.getInt("quantity");
			int index = result.getInt("index");
			
			String name = result.getString("name");
			String sellPrice = result.getString("sellPrice");
			return new BagMaterial(uniqueId, roleId, id, name, typeId, quantity, index, sellPrice);
		}
	};
}
