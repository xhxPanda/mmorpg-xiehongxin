package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.domain.ItemDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ItemXmlResolutionManager;

public class ItemMasterialHandler extends AbstractMaterialHandler {

	private Map<Integer, ItemDomain> itemDomainMap;

	public ItemMasterialHandler() {
		itemDomainMap = ItemXmlResolutionManager.INSTANCE.resolution();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		ItemDomain itemDomain = itemDomainMap.get(id);
		UserItem userItem = new UserItem(role.getId(), itemDomain.getName(), id, needNum, 0, System.currentTimeMillis(),
				itemDomain.getEffectAttribuate(), itemDomain.getBuffs(), itemDomain.getCd());
		role.addMaterial(userItem);

		MaterialDao.INSTANCE.updateRoleItem(userItem);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		if (!role.isContainMaterial(id)) {
			return ReplyDomain.FAILE;
		}

		UserItem material = (UserItem) role.findMaterial(id);

		if (material == null || needNum > material.getQuantity()) {
			return ReplyDomain.NOT_ENOUGH;
		}
		MaterialDao.INSTANCE.updateRoleItem(material);
		role.decMaterial(id, needNum);

		return null;
	}

}
