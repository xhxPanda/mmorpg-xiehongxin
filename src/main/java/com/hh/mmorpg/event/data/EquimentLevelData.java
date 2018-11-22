package com.hh.mmorpg.event.data;

import java.util.Map;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.EventData;

/**
 * 判断用户身上的装备是否全部达到xxx级
 * 
 * @author xhx
 *
 */
public class EquimentLevelData extends EventData {

	private Role role;
	private Map<Integer, UserEquipment> userEquipmentMap;

	public EquimentLevelData(Role role, Map<Integer, UserEquipment> userEquipmentMap) {
		this.role = role;
		this.userEquipmentMap = userEquipmentMap;
	}

	public Role getRole() {
		return role;
	}

	public Map<Integer, UserEquipment> getUserEquipmentMap() {
		return userEquipmentMap;
	}

}
