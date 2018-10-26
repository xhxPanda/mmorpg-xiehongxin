package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.domain.ClothesDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ClothesXmlResolutionManager;

public class EquipmentMaterialHandle extends AbstractMaterialHandler {

	private Map<Integer, ClothesDomain> clothesDomainMap;

	public EquipmentMaterialHandle() {
		clothesDomainMap = ClothesXmlResolutionManager.INSTANCE.resolution();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);

		if (role.isContainMaterial(id)) {
			return ReplyDomain.REPEAT_CLOTHES;
		}
		ClothesDomain clothesDomain = clothesDomainMap.get(id);

		UserEquipment userEquipment = new UserEquipment(role.getId(), clothesDomain.getName(), id,
				clothesDomain.getMaxDurability(), clothesDomain.getMaxDurability(), clothesDomain.getAttributes(),
				System.currentTimeMillis());

		role.addMaterial(userEquipment);

		MaterialDao.INSTANCE.updateRoleEquiment(userEquipment);
		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);
		role.decMaterial(id, 1);
		MaterialDao.INSTANCE.deleteRoleEquiment(role.getId(), id);
		return ReplyDomain.SUCCESS;
	}

}
