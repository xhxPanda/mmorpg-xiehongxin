package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.domain.ClothesDomain;
import com.hh.mmorpg.domain.Material;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserClothes;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ClothesXmlResolutionManager;

public class ClothesMaterialHandle extends AbstractMaterialHandler {

	private Map<Integer, ClothesDomain> clothesDomainMap;

	public ClothesMaterialHandle() {
		clothesDomainMap = ClothesXmlResolutionManager.INSTANCE.resolution();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr, int reason) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);

		if (role.isContainMaterial(id)) {
			return ReplyDomain.REPEAT_CLOTHES;
		}
		ClothesDomain clothesDomain = clothesDomainMap.get(id);

		UserClothes userClothes = new UserClothes(role.getId(), clothesDomain.getName(), id,
				clothesDomain.getMaxDurability(), clothesDomain.getMaxDurability(), clothesDomain.getAttributes(),
				System.currentTimeMillis());

		role.addMaterial(userClothes);
		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr, int reason) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		if (!role.isContainMaterial(id)) {
			return ReplyDomain.FAILE;
		}

		Material material = role.findMaterial(id);

		if (material == null || needNum > material.getQuantity()) {
			return ReplyDomain.NOT_ENOUGH;
		}

		role.decMaterial(id, needNum);

		return ReplyDomain.SUCCESS;
	}

}
