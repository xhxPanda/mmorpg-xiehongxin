package com.hh.mmorpg.server.equiment;

import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;

public class UserEquimentService {

	public static final UserEquimentService INSTANCE = new UserEquimentService();

	public ReplyDomain showEquiment(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("装备栏", role.getEquipmentMap().values());
		return replyDomain;
	}

	public ReplyDomain wearEquiment(User user, int equimentId) {
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		UserEquipment equipment = (UserEquipment) role.getMaterial(MaterialType.EQUIPMENT_TYPE.getId(), equimentId);
		if (equipment == null) {
			return ReplyDomain.FAILE;
		}

		role.setEquipment(equipment);

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain takeOffEquiment(User user, int equimentType) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		role.takeOffEquiment(equimentType);
		return ReplyDomain.SUCCESS;
	}

}