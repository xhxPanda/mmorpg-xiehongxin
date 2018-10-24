package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Material;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.result.ReplyDomain;

public abstract class AbstractMaterialHandler {

	public abstract ReplyDomain gainMaterial(Role role, String[] material);

	public abstract ReplyDomain decMasterial(Role role, String[] material);

	public ReplyDomain checkDecMaterial(Role role, String[] materialStr) {
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);
		
		if (!role.isContainMaterial(id)) {
			return ReplyDomain.NOT_ENOUGH;
		}
		Material material = role.getMaterial(id);
		if (material == null || needNum > material.getQuantity()) {
			return ReplyDomain.NOT_ENOUGH;
		}
		return ReplyDomain.SUCCESS;
	}

}
