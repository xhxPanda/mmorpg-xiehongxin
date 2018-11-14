package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.domain.EquimentDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.handler.xmlManager.EquimentXmlResolutionManager;

public class EquipmentMaterialHandle extends AbstractMaterialHandler<UserEquipment> {

	private Map<Integer, EquimentDomain> equimentDomainMap;

	public EquipmentMaterialHandle() {
		equimentDomainMap = EquimentXmlResolutionManager.INSTANCE.resolution();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(materialStr[1]);

		EquimentDomain equimentDomain = equimentDomainMap.get(id);

		UserEquipment userEquipment = new UserEquipment(role.getId(), equimentDomain.getName(), id,
				equimentDomain.getMaxDurability(), equimentDomain.getMaxDurability(), equimentDomain.getAttributes(),
				System.currentTimeMillis(), equimentDomain.getSellPrice(), -1, equimentDomain.getClothesType());

		return role.addMaterial(userEquipment);

	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);
		role.decMaterial(Integer.parseInt(materialStr[0]), id, 1);
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain useMaterial(Role role, UserEquipment equipment) {
		role.setEquipment(equipment);
		return ReplyDomain.SUCCESS;
	}

	@Override
	public void persistence(UserEquipment equipment) {
		// TODO Auto-generated method stub
		MaterialDao.INSTANCE.updateRoleEquiment(equipment);
	}

	@Override
	public int getPileNum(int materialId) {
		// TODO Auto-generated method stub
		return 1;
	}

}
