package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 
 * @author xhx
  *  用户角色
 *
 */

public class Role extends LivingThing {

	private int userId;
	private int id;
	private String name;
	private int roleId;

	private Map<Integer, Material> materialMap;
	
	// 装备栏
	private Map<Integer, UserEquipment> equipmentMap;

	public Role(int userId, int id, String name, int roleId) {
		super(roleId, id);
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.roleId = roleId;
		this.materialMap = new HashMap<>();

		this.equipmentMap = new HashMap<>();
		for (int i = 1; i <= 7; i++) {
			equipmentMap.put(i, null);
		}
	}

	public int getUserId() {
		return userId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getRoleId() {
		return roleId;
	}

	public static final ResultBuilder<Role> BUILDER = new ResultBuilder<Role>() {

		@Override
		public Role build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int userId = result.getInt(1);
			int id = result.getInt(2);
			int roleId = result.getInt(3);
			String name = result.getString(4);
			return new Role(userId, id, name, roleId);
		}
	};

	@Override
	public String toString() {
		return "Role [userId=" + userId + ", id=" + id + ", name=" + name + ", roleId=" + roleId + ", hp="
				+ equipmentMap.get(3) + ", hp=" + equipmentMap.get(4) + "]";
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub

	}

	public void addMaterial(Material material) {
		if (materialMap.get(material.getId()) == null) {
			materialMap.put(material.getId(), material);
		} else {
			materialMap.get(material.getId()).changeQuantity(material.getQuantity());
		}
	}

	public Material findMaterial(int materialId) {
		// TODO Auto-generated method stub
		return materialMap.get(materialId);
	}

	public boolean isContainMaterial(int materialId) {
		return materialMap.containsKey(materialId);
	}

	public void setEquipment(UserEquipment clothes) {
		int clothesType = clothes.getType();
		if (equipmentMap.containsKey(clothesType)) {
			UserEquipment userClothes = equipmentMap.remove(clothesType);

			// 卸下服装
			for (Entry<Integer, Integer> entry : userClothes.getAttributeMap().entrySet()) {
				effectAttribute(entry.getKey(), -entry.getValue());
			}
			userClothes.setInUsed(false);
			materialMap.put(userClothes.getId(), userClothes);
		}

		// 装备服装
		equipmentMap.put(clothes.getType(), clothes);
		for (Entry<Integer, Integer> entry : clothes.getAttributeMap().entrySet()) {
			effectAttribute(entry.getKey(), entry.getValue());
		}
		clothes.setInUsed(true);
		materialMap.remove(clothes.getId());
	}

	public void addMaterial(Map<Integer, Material> materialMap) {
		materialMap.putAll(materialMap);
	}

	public Map<Integer, UserEquipment> getEquipmentMap() {
		return equipmentMap;
	}

	public void setEquipmentMap(Map<Integer, UserEquipment> equipmentMap) {
		this.equipmentMap = equipmentMap;
	}

	public void decMaterial(int id, int quantity) {
		Material material = materialMap.get(id);
		if (material.getQuantity() == 1) {
			materialMap.remove(id);
		} else {
			material.changeQuantity(quantity);
		}
	}

	public Material getMaterial(int materialId) {
		return materialMap.get(materialId);
	}

}
