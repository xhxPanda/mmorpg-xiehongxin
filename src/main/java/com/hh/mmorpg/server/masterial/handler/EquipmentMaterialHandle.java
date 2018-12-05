package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.EquimentDomain;
import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.equiment.UserEquimentService;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.handler.xmlManager.EquimentXmlResolutionManager;

public class EquipmentMaterialHandle extends AbstractMaterialHandler {

	private Map<Integer, EquimentDomain> equimentDomainMap;

	public EquipmentMaterialHandle() {
		equimentDomainMap = EquimentXmlResolutionManager.INSTANCE.resolution();
	}

	/**
	 * 新添加装备
	 */
	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(materialStr[1]);
		EquimentDomain equimentDomain = equimentDomainMap.get(id);

		int roleId = role.getId();

		int uniqueId = IncrementManager.INSTANCE.increase("uniqueMaterial");

		// 在装备列表中新增一个新装备
		UserEquipment userEquipment = new UserEquipment(role.getId(), uniqueId, equimentDomain.getId(),
				equimentDomain.getName(), equimentDomain.getSellPrice(), equimentDomain.getEquimentLevel(),
				equimentDomain.getEquimentSource(), equimentDomain.getMaxDurability(),
				equimentDomain.getMaxDurability(), equimentDomain.getEquimentType(), equimentDomain.getAttributes(), false);
		
		// 持久化
		MaterialDao.INSTANCE.updateRoleEquiment(userEquipment);
		
		ReplyDomain replyDomain = role.addMaterial(new BagMaterial(uniqueId, roleId, equimentDomain.getId(),
				equimentDomain.getName(), MaterialType.EQUIPMENT_TYPE.getId(), 1, 0, equimentDomain.getSellPrice()));

		if (replyDomain.isSuccess()) {
			UserEquimentService.INSTANCE.addEquiment(userEquipment);
		}

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		return ReplyDomain.FAILE;
	}

	@Override
	public int getPileNum(int materialId) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public ReplyDomain useMaterial(Role role, int uniqueId) {
		// TODO Auto-generated method stub

		UserEquipment equipment = UserEquimentService.INSTANCE.getUserEquiment(uniqueId);

		role.setEquipment(equipment);
		return ReplyDomain.SUCCESS;
	}

}
