package com.hh.mmorpg.server.masterial.handler;

import java.util.Map.Entry;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.ItemDomain;
import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.GetMaterialData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.item.ItemService;
import com.hh.mmorpg.server.masterial.MaterialExtension;
import com.hh.mmorpg.server.skill.SkillService;

/**
 * 处理道具类的物品的类
 * 
 * @author xhx
 *
 */
public class ItemMasterialHandler extends AbstractMaterialHandler {

	public ItemMasterialHandler() {
	}

	@Override
	public ReplyDomain gainMaterial(User user, Role role, String[] materialStr) {

		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		ItemDomain itemDomain = ItemService.INSTANCE.getItemDomain(id);
		BagMaterial bagMaterial = new BagMaterial(id, role.getId(), id, itemDomain.getName(),
				MaterialType.ITEM_TYPE.getId(), needNum, 0, itemDomain.getSellPrice());
		ReplyDomain replyDomain = role.addMaterial(bagMaterial);

		// 抛出获得物品的事件
		GetMaterialData data = new GetMaterialData(role, bagMaterial, 1);
		EventHandler.INSTANCE.invodeMethod(GetMaterialData.class, data);

		return replyDomain;
	}

	@Override
	public ReplyDomain decMasterial(User user, Role role, String[] materialStr) {

		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		ReplyDomain replyDomain = role.decMaterial(id, needNum);

		if (replyDomain.isSuccess()) {
			ItemDomain itemDomain = ItemService.INSTANCE.getItemDomain(id);

			ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
			notify.setStringDomain("cmd", "减少" + needNum + itemDomain.getName());
			MaterialExtension.notifyMaterialDec(user, notify);
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain useMaterial(Role role, int itemId) {
		ItemDomain itemDomain = ItemService.INSTANCE.getItemDomain(itemId);
		long cd = itemDomain.getCd();

		UserItem userItem = ItemService.INSTANCE.getUserItem(role.getId(), itemId);
		if (userItem != null && System.currentTimeMillis() - userItem.getLastUsedTime() < cd) {
			return ReplyDomain.IN_CD;
		}

		for (Entry<Integer, Integer> entry : itemDomain.getEffectAttribuate().entrySet()) {
			role.effectAttribute(0, entry.getKey(), entry.getValue(), "使用道具");
		}
		if (itemDomain.getBuffs().size() != 0) {
			for (Integer buffId : itemDomain.getBuffs()) {
				SkillService.INSTANCE.addBuff(role, buffId);
			}
		}

		if (userItem == null) {
			userItem = new UserItem(role.getId(), itemId, 0);
		}
		userItem.setLastUsedTime(System.currentTimeMillis());
		ItemService.INSTANCE.addUserItem(userItem);
		return ReplyDomain.SUCCESS;
	}

	@Override
	public int getPileNum(int itemId) {

		ItemDomain itemDomain = ItemService.INSTANCE.getItemDomain(itemId);
		return itemDomain.getPileNum();
	}

}
