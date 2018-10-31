package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;

public class MoneyMaterialHandler extends AbstractMaterialHandler<UserTreasure> {

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

	@Override
	public void persistence(UserTreasure material) {
		// TODO Auto-generated method stub
		MaterialDao.INSTANCE.updateRoleTreasure(material);
	}

}