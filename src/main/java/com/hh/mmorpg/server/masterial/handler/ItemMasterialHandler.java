package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.domain.Material;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.domain.ItemDomain;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ItemXmlResolutionManager;

public class ItemMasterialHandler extends AbstractMaterialHandler {

	private Map<Integer, ItemDomain> itemDomainMap;

	public ItemMasterialHandler() {
		itemDomainMap = ItemXmlResolutionManager.INSTANCE.resolution();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr, int reason) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		ItemDomain itemDomain = itemDomainMap.get(id);
		role.addMaterial(new UserItem(role.getId(), itemDomain.getName(), id, needNum, 0,
				System.currentTimeMillis(), itemDomain.getEffectAttribuate(), itemDomain.getBuffs(),
				itemDomain.getCd()));

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

		return null;
	}

}
