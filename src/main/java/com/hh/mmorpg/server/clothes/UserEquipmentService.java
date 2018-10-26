package com.hh.mmorpg.server.clothes;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;

public class UserEquipmentService {
	
	public static final UserEquipmentService INSTANCE = new UserEquipmentService();
	
	public ReplyDomain setUserClothes(User user, int clothesId) {
		int userId = user.getUserId();
		
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		
		UserEquipment equipment = (UserEquipment) role.findMaterial(clothesId);
		if(equipment == null) {
			return ReplyDomain.FAILE;
		}
		
		role.setEquipment(equipment);
		
		return ReplyDomain.SUCCESS;
	}
}