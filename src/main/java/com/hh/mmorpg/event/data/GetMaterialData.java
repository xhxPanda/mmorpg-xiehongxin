package com.hh.mmorpg.event.data;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventData;

/**
 * 获得物品
 * 
 * @author xhx
 *
 */
public class GetMaterialData extends EventData {

	private Role role;
	private BagMaterial bagMaterial;
	private int num;

	public GetMaterialData(Role role, BagMaterial bagMaterial, int num) {
		this.role = role;
		this.bagMaterial = bagMaterial;
		this.num = num;
	}

	public Role getRole() {
		return role;
	}

	public BagMaterial getBagMaterial() {
		return bagMaterial;
	}

	public int getNum() {
		return num;
	}

}
