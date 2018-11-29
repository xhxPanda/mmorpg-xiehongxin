package com.hh.mmorpg.server.equiment;

import java.util.HashMap;
import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.role.RoleService;

public class UserEquimentService {

	public static final UserEquimentService INSTANCE = new UserEquimentService();

	private HashMap<Integer, UserEquipment> equimentMap;

	private UserEquimentService() {
		this.equimentMap = new HashMap<>();

		List<UserEquipment> list = MaterialDao.INSTANCE.getAllUserEquiment();
		if (list != null && list.size() != 0) {
			for (UserEquipment userEquipment : list) {
				equimentMap.put(userEquipment.getUniqueId(), userEquipment);
			}
		}
	}

	public ReplyDomain showEquiment(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("装备栏", role.getEquipmentMap().values());
		return replyDomain;
	}

	public ReplyDomain takeOffEquiment(User user, int equimentType) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		role.takeOffEquiment(equimentType);
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 在武器列表中增加一个
	 * 
	 * @param userEquipment
	 */
	public void addEquiment(UserEquipment userEquipment) {
		equimentMap.put(userEquipment.getUniqueId(), userEquipment);
	}

	public UserEquipment getUserEquiment(int uniqueId) {
		return equimentMap.get(uniqueId);
	}

}