package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.domain.UserTreasureType;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;

public class TreasureMaterialHandler extends AbstractMaterialHandler<UserTreasure> {

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int materialId = Integer.parseInt(materialStr[1]);
		int num = Integer.parseInt(materialStr[2]);
		UserTreasureType userTreasureType = UserTreasureType.getUserTreasureType(materialId);
		UserTreasure material = new UserTreasure(role.getId(), userTreasureType.getName(), materialId, num);

		role.addMaterial(material);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int materialId = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		if (!role.isContainMaterial(Integer.parseInt(materialStr[0]), materialId)) {
			return ReplyDomain.FAILE;
		}

		UserTreasure material = (UserTreasure) role.getMaterial(MaterialType.TREASURE_TYPE_ID, materialId);

		if (material == null || needNum > material.getQuantity()) {
			return ReplyDomain.NOT_ENOUGH;
		}
		role.decMaterial(MaterialType.TREASURE_TYPE_ID, materialId, needNum);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public void persistence(UserTreasure material) {
		// TODO Auto-generated method stub
		MaterialDao.INSTANCE.updateRoleTreasure(material);
	}

}