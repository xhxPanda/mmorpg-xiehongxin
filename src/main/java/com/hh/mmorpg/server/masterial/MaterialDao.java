package com.hh.mmorpg.server.masterial;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.jdbc.JDBCManager;

public class MaterialDao {

	public static final MaterialDao INSTANCE = new MaterialDao();

	private static final String UPDATE_TREASURE = "INSERT INTO roleTreasure0 (roleId, treasureId, name, quantity) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity = ?";
	private static final String UPDATE_ITEM = "INSERT INTO roleitem0 (roleId, itemId, name, quantity, gainTime, lastUsedTime, cd, effectAttribute, buffs) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity = ?";
	private static final String UPDATE_EQUIMENT = "INSERT INTO roleequiment0 (roleId, equimentId, name, gainTime, effectAttribute, maxDurability, durability) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String DELETE_EQUIMENT = "DELETE FROM roleequiment0 WHERE roleId = ? AND equimentId = ?";

	private static final String SELECT_ROLE_CLOTHES = "SELECT * FROM roleEquiment0 WHERE roleId = ?";

	private static final String SELECT_ROLE_ITEM = "SELECT * FROM roleItem0 WHERE roleId = ?";

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
	public List<UserEquipment> getAllUserClothes(int roleId) {
		List<UserEquipment> list = null;
		try {
			list = (List<UserEquipment>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_ROLE_CLOTHES,
					new Object[] { roleId }, UserEquipment.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public int updateRoleTreasure(UserTreasure treasure) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_TREASURE, new Object[] { treasure.getRoleId(),
				treasure.getId(), treasure.getName(), treasure.getQuantity(), treasure.getQuantity() });
	}

	public int updateRoleItem(UserItem userItem) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ITEM,
				new Object[] { userItem.getRoleId(), userItem.getId(), userItem.getName(), userItem.getQuantity(),
						userItem.getGainTime(), userItem.getLastUsedTime(), userItem.getCd(),
						userItem.getAttributeStr(), userItem.getBuffStr(), userItem.getQuantity() });
	}

	public int updateRoleEquiment(UserEquipment equipment) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_EQUIMENT,
				new Object[] { equipment.getRoleId(), equipment.getId(), equipment.getName(), equipment.getGainTime(),
						equipment.getAttributeStr(), equipment.getMaxDurability(), equipment.getDurability() });
	}

	public int deleteRoleEquiment(int roleId, int equipmentId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_EQUIMENT,
				new Object[] { roleId, equipmentId });
	}
}
