package com.hh.mmorpg.domain;

/**
 * 
 * @author xhx
 * 物品基类
 */
public class Material {

	private int roleId; // roleId
	private int id;    // id
	private String name;
	private int typeId; // 类型Id

	public Material(int roleId, int id,String name,  int typeId) {
		this.id = id;
		this.roleId = roleId;
		this.name = name;
		this.typeId = typeId;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getRoleId() {
		return roleId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
