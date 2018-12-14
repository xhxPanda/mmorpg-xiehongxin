package com.hh.mmorpg.server.masterial.handler;

import java.util.List;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;

public abstract class AbstractMaterialHandler {

	public abstract ReplyDomain gainMaterial(User user, Role role, String[] material);

	public abstract ReplyDomain decMasterial(User user, Role role, String[] material);

	public abstract ReplyDomain useMaterial(Role role, int uniqueId);

	public ReplyDomain checkDecMaterial(Role role, String[] materialStr) {
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		List<BagMaterial> materials = role.getMaterialById(id);
		if (materials.size() == 0) {
			return ReplyDomain.NOT_ENOUGH;
		}

		int totalNum = 0;
		for (BagMaterial m : materials) {
			totalNum += m.getQuantity();
		}

		if (totalNum < needNum) {
			return ReplyDomain.NOT_ENOUGH;
		}

		return ReplyDomain.SUCCESS;
	}

	public abstract int getPileNum(int id);
}
