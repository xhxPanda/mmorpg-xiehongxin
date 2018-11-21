package com.hh.mmorpg.domain;

/**
 * 
 * @author xhx
 * 物品基类
 */
public class Material {

	private int roleId; // roleId
	private String name; // 名称
	private int id;    // id
	private int type; // 类型Id
	private String typeName;// 类型名称

	public Material(int roleId, String name, int id, int type, String typeName) {
		this.name = name;
		this.id = id;
		this.type = type;
		this.typeName = typeName;
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

	public int getRoleId() {
		return roleId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
