package com.hh.mmorpg.server.masterial;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.jdbc.JDBCManager;

public class MaterialDao {

	public static final MaterialDao INSTANCE = new MaterialDao();

	private static final String UPDATE_TREASURE = "REPLACE INTO roleTreasure0 (roleId, id, name, quantity) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_ITEM = "INSERT INTO `roleitem0` (`roleId`, `id`, `name`, `quantity`, `gainTime`, `lastUsedTime`, `cd`, `effectAttribute`, `buffs`, `sellPrice`, `index`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity = ?";
	private static final String UPDATE_EQUIMENT = "REPLACE INTO `roleequiment0` (`roleId`, `equimentId`, `name`, `gainTime`, `effectAttribute`, `maxDurability`, `durability`, `isInUsed`, `type`, `index`, `SellPrice`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String SELECT_ROLE_EQUIMENT = "SELECT * FROM roleEquiment0 WHERE roleId = ?";
	private static final String SELECT_ROLE_ITEM = "SELECT * FROM roleItem0 WHERE roleId = ?";
	private static final String SELECT_ROLE_TREASURE = "SELECT * FROM roletreasure0 WHERE roleId = ?";

	private static final String DELETE_MATERIAL = "DELETE FROM %s WHERE WHERE roleId = ? AND index = ?";

	private static Map<Integer, String> BAG_MATERIAL_TABLE_NAME_MAP = new HashMap<>();

	private MaterialDao() {
		BAG_MATERIAL_TABLE_NAME_MAP = new HashMap<>();
		BAG_MATERIAL_TABLE_NAME_MAP.put(MaterialType.EQUIPMENT_TYPE.getId(), "roleEquiment0");
		BAG_MATERIAL_TABLE_NAME_MAP.put(MaterialType.ITEM_TYPE.getId(), "roleItem0");
	}

	@SuppressWarnings("unchecked")
	public List<UserTreasure> getAllTreasure(int roleId) {
		List<UserTreasure> list = null;
		try {
			list = (List<UserTreasure>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_TREASURE,
					new Object[] { roleId }, UserTreasure.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<UserItem> getAllItem(int roleId) {
		List<UserItem> list = null;
		try {
			list = (List<UserItem>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_ITEM,
					new Object[] { roleId }, UserItem.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<UserEquipment> getAllUserEquiment(int roleId) {
		List<UserEquipment> list = null;
		try {
			list = (List<UserEquipment>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_EQUIMENT,
					new Object[] { roleId }, UserEquipment.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int updateRoleTreasure(UserTreasure treasure) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_TREASURE, new Object[] { treasure.getRoleId(),
				treasure.getId(), treasure.getName(), treasure.getQuantity()});
	}

	public int updateRoleItem(UserItem userItem) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ITEM,
				new Object[] { userItem.getRoleId(), userItem.getId(), userItem.getName(), userItem.getQuantity(),
						userItem.getGainTime(), userItem.getLastUsedTime(), userItem.getCd(),
						userItem.getAttributeStr(), userItem.getBuffStr(), userItem.getSellPrice(), userItem.getIndex(), userItem.getQuantity() });
	}

	public int updateRoleEquiment(UserEquipment equipment) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_EQUIMENT,
				new Object[] { equipment.getRoleId(), equipment.getId(), equipment.getName(), equipment.getGainTime(),
						equipment.getAttributeStr(), equipment.getMaxDurability(), equipment.getDurability(),
						equipment.isInUsed(), equipment.getEquimentType(), equipment.getIndex(),
						equipment.getSellPrice() });
	}

	public int deleteMaterial(int typeId, int roleId, int index) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(
				String.format(DELETE_MATERIAL, BAG_MATERIAL_TABLE_NAME_MAP.get(typeId)),
				new Object[] { roleId, index });
	}
}
