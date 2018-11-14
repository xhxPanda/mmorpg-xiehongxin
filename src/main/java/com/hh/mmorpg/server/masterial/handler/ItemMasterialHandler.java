package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.domain.ItemDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ItemXmlResolutionManager;
import com.hh.mmorpg.server.skill.SkillService;

public class ItemMasterialHandler extends AbstractMaterialHandler<UserItem> {

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
		UserItem userItem = new UserItem(role.getId(), itemDomain.getName(), id, needNum, System.currentTimeMillis(), 0,
				itemDomain.getEffectAttribuate(), itemDomain.getBuffs(), itemDomain.getCd(), itemDomain.getSellPrice(),
				-1);

		return role.addMaterial(userItem);
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		role.decMaterial(Integer.parseInt(materialStr[0]), id, needNum);
		
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain useMaterial(Role role, UserItem userItem) {

		for (Entry<Integer, Integer> entry : userItem.getEffectAttributeMap().entrySet()) {
			role.effectAttribute(entry.getKey(), entry.getValue(), "使用道具");
		}
		if (userItem.getBuffList().size() != 0) {
			for (Integer buffId : userItem.getBuffList()) {
				SkillService.INSTANCE.addBuff(role, buffId);
			}

		}
		return ReplyDomain.SUCCESS;
	}

	@Override
	public void persistence(UserItem userItem) {
		// TODO Auto-generated method stub
		MaterialDao.INSTANCE.updateRoleItem(userItem);
	}

	@Override
	public int getPileNum(int materialId) {
		// TODO Auto-generated method stub
		return itemDomainMap.get(materialId).getPileNum();
	}

}
