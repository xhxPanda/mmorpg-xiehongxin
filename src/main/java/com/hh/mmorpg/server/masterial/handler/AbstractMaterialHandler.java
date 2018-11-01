package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Material;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.result.ReplyDomain;

public abstract class AbstractMaterialHandler<T extends Material> {

	public abstract ReplyDomain gainMaterial(Role role, String[] material);

	public abstract ReplyDomain decMasterial(Role role, String[] material);

	public ReplyDomain checkDecMaterial(Role role, String[] materialStr) {
		int typeId = Integer.parseInt(materialStr[0]);
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		if (!role.isContainMaterial(typeId, id)) {
			return ReplyDomain.NOT_ENOUGH;
		}
		Material material = role.getMaterial(typeId, id);
		if (material == null || needNum > material.getQuantity()) {
			return ReplyDomain.NOT_ENOUGH;
		}
		return ReplyDomain.SUCCESS;
	}

	public abstract void persistence(T material);

}
