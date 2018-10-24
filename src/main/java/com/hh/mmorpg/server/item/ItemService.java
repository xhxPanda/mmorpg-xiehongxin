package com.hh.mmorpg.server.item;

import java.util.Map.Entry;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MasterialService;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.skill.SkillService;

public class ItemService {

	public static final ItemService INSATNCE = new ItemService();

	private ItemService() {
	}

	public ReplyDomain useItem(User user, String itemStr) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		UserItem userItem = (UserItem) role.getMaterial(Integer.parseInt(itemStr.substring(0, 1)));
		if(userItem.isPartCD()) {
			return ReplyDomain.FAILE;
		}

		ReplyDomain replyDomain = MasterialService.INSTANCE.decMasterial(user, role, itemStr);
		if (!replyDomain.isSuccess()) {
			return ReplyDomain.FAILE;
		}

		for (Entry<Integer, Integer> entry : userItem.getEffectAttributeMap().entrySet()) {
			role.effectAttribute(entry.getKey(), entry.getValue());
		}
		if (userItem.getBuffList().size() != 0) {
			for (Integer buffId : userItem.getBuffList()) {
				SkillService.INSTANCE.addBuff(role, buffId);
			}

		}
		return ReplyDomain.SUCCESS;
	}

}
