package com.hh.mmorpg.server.masterial;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.domain.UserItem;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.jdbc.JDBCManager;

public class MaterialDao {

	public static final MaterialDao INSTANCE = new MaterialDao();

	private static final String UPDATE_BAGMATERIAL = "REPLACE INTO `rolematerial` (`roleId`, `uniqueId`, `index`, `id`, `quantity`, `SellPrice`, `name`, `typeId`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_TREASURE = "REPLACE INTO roleTreasure0 (roleId, id, name, quantity) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_ITEM = "INSERT INTO `roleitem0` (`roleId`, `itemId`, `lastUsedTime`) VALUES (?, ?, ?)";
	private static final String UPDATE_EQUIMENT = "REPLACE INTO `equiment0` (`uniqueId`, `roleId`, `equimentId`, `name`, `effectAttribute`, `equimentType`, `maxDurability`, `durability`, `SellPrice`, `equimentLevel`, `equimentSource`, `isInUsed`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SELECT_ROLE_ITEM = "SELECT * FROM roleItem0 WHERE roleId = ?";
	private static final String SELECT_ROLE_TREASURE = "SELECT * FROM roletreasure0 WHERE roleId = ?";

	private static final String SELECT_ROLE_EQUIMENT = "SELECT * FROM equiment0 WHERE roleId = ? AND isInUsed = 1";
	private static final String SELECT_EQUIMENT = "SELECT * FROM equiment0";

	private static final String SELECT_USER_MATERIAL = "SELECT * FROM roleMaterial WHERE roleId = ?";
	private static final String DELETE_MATERIAL = "DELETE FROM %s WHERE WHERE roleId = ? AND index = ?";

	private MaterialDao() {
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
	public List<UserEquipment> getAllUserEquiment() {
		List<UserEquipment> list = null;
		try {
			list = (List<UserEquipment>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_EQUIMENT,
					new Object[] {}, UserEquipment.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int updateRoleTreasure(UserTreasure treasure) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_TREASURE,
				new Object[] { treasure.getRoleId(), treasure.getId(), treasure.getName(), treasure.getQuantity() });
	}

	public int updateRoleItem(UserItem userItem) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_ITEM,
				new Object[] { userItem.getRoleId(), userItem.getMaterialId(), userItem.getLastUsedTime() });
	}

	public int updateRoleEquiment(UserEquipment equipment) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_EQUIMENT,
				new Object[] { equipment.getUniqueId(), equipment.getRoleId(), equipment.getMaterialId(),
						equipment.getName(), equipment.getAttributeStr(), equipment.getEquimentType(),
						equipment.getMaxDurability(), equipment.getDurability(), equipment.getSellPrice(),
						equipment.getEquimentLevel(), equipment.getEquimentSource(), equipment.isInUsed() });
	}

	public int deleteMaterial(int typeId, int roleId, int index) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_MATERIAL, new Object[] { roleId, index });
	}

	@SuppressWarnings("unchecked")
	public List<BagMaterial> getallUserBagMaterial(int roleId) {
		// TODO Auto-generated method stub
		List<BagMaterial> list = null;
		try {
			list = (List<BagMaterial>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_USER_MATERIAL,
					new Object[] { roleId }, BagMaterial.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<UserEquipment> getRoleEquiment(int roleId) {
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

	public int updateRoleMaterial(BagMaterial bagMaterial) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_BAGMATERIAL,
				new Object[] { bagMaterial.getRoleId(), bagMaterial.getUniqueId(), bagMaterial.getIndex(),
						bagMaterial.getId(), bagMaterial.getQuantity(),
						bagMaterial.getSellPrice(), bagMaterial.getName(), bagMaterial.getTypeId() });
	}
}
