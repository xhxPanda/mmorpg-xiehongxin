package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.result.ReplyDomain;

public class MoneyMaterialHandler extends AbstractMaterialHandler {

	@Override
	public ReplyDomain gainMaterial(Role role, String[] material) {
		// TODO Auto-generated method stub
		int materialId = Integer.parseInt(material[1]);
		int num = Integer.parseInt(material[2]);
		role.getMaterial(materialId).changeQuantity(num);
		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] material) {
		// TODO Auto-generated method stub
		int materialId = Integer.parseInt(material[1]);
		int num = Integer.parseInt(material[2]);
		role.getMaterial(materialId).changeQuantity(-num);
		return ReplyDomain.SUCCESS;
	}

}