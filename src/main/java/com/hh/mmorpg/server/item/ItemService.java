package com.hh.mmorpg.server.item;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.ItemDomain;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.server.masterial.handler.xmlManager.ItemXmlResolutionManager;

public class ItemService {

	public static final ItemService INSTANCE = new ItemService();

	private Map<Integer, Map<Integer, UserItem>> roleItem;
	private Map<Integer, ItemDomain> itemDomainMap;

	private ItemService() {
		itemDomainMap = ItemXmlResolutionManager.INSTANCE.resolution();
	}

	public ItemDomain getItemDomain(int id) {
		return itemDomainMap.get(id);
	}

	public UserItem getUserItem(int roleId, int id) {
		Map<Integer, UserItem> map = roleItem.get(roleId);
		if (map == null) {
			map = new HashMap<>();
		}

		return roleItem.get(roleId).get(id);
	}

	public void addUserItem(UserItem userItem) {
		Map<Integer, UserItem> map = roleItem.get(userItem.getRoleId());
		map.put(userItem.getMaterialId(), userItem);
	}

}
