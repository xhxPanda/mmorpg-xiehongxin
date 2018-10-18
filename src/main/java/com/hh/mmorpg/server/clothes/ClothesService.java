package com.hh.mmorpg.server.clothes;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserClothes;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;

public class ClothesService {
	
	public static final ClothesService INSTANCE = new ClothesService();
	
	public ReplyDomain setUserClothes(User user, int clothesId) {
		int userId = user.getUserId();
		
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		
		UserClothes clothes = (UserClothes) role.findMaterial(clothesId);
		if(clothes == null) {
			return ReplyDomain.FAILE;
		}
		
		role.setEquipment(clothes);
		
		return ReplyDomain.SUCCESS;
	}

}