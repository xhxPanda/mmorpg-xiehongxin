package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.scene.SceneExtension;
import com.hh.mmorpg.server.scene.SceneService;

/**
 * 
 * @author xhx 用户角色
 *
 */

public class Role extends LivingThing {

	private int userId;
	private int id;
	private String name;
	private int roleId;

	private Map<Integer, Map<Integer, Material>> materialMap;

	// 装备栏
	private Map<Integer, UserEquipment> equipmentMap;

	public Role(int userId, int id, String name, int roleId) {
		super(roleId, id);
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.roleId = roleId;
		this.materialMap = new HashMap<>();
		this.equipmentMap = new HashMap<>();
	}

	public int getUserId() {
		return userId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getRoleId() {
		return roleId;
	}

	public static final ResultBuilder<Role> BUILDER = new ResultBuilder<Role>() {

		@Override
		public Role build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int userId = result.getInt(1);
			int id = result.getInt(2);
			int roleId = result.getInt(3);
			String name = result.getString(4);
			return new Role(userId, id, name, roleId);
		}
	};

	@Override
	public String toString() {
		return "Role [userId=" + userId + ", id=" + id + ", name=" + name + ", roleId=" + roleId + ", hp="
				+ equipmentMap.get(3) + ", hp=" + equipmentMap.get(4) + "]";
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub

	}

	public void addMaterial(Material material) {
		Map<Integer, Material> materials = materialMap.get(material.getType());
		if (materials == null) {
			materials = new HashMap<>();
			materialMap.put(material.getType(), materials);

		}
		Material m = materials.get(material.getId());
		if (m != null) {
			materials.get(material.getId()).changeQuantity(material.getQuantity());
		} else {
			materials.put(material.getId(), material);
		}
	}

	public boolean isContainMaterial(int materialType, int materialId) {
		if (materialMap.get(materialType) == null || materialMap.get(materialType).size() == 0) {
			return false;
		}
		return materialMap.get(materialType).containsKey(materialId);
	}

	public void setEquipment(UserEquipment equipment) {
		int equipmentType = equipment.getType();
		if (equipmentMap.size() != 0 && equipmentMap.get(equipmentType).getId() == equipment.getId()) {
			return;
		}

		takeOffEquiment(equipmentType);

		// 装备服装
		equipmentMap.put(equipment.getType(), equipment);
		for (Entry<Integer, Integer> entry : equipment.getAttributeMap().entrySet()) {
			effectAttribute(entry.getKey(), entry.getValue());
		}
		equipment.setInUsed(true);
		MaterialDao.INSTANCE.updateRoleEquiment(equipment);
		if (materialMap.get(equipmentType) != null)
			materialMap.get(equipmentType).remove(equipment.getId());
	}

	public void takeOffEquiment(int equipmentType) {
		if (equipmentMap.containsKey(equipmentType)) {
			UserEquipment userEquipment = equipmentMap.remove(equipmentType);

			// 卸下服装
			for (Entry<Integer, Integer> entry : userEquipment.getAttributeMap().entrySet()) {
				effectAttribute(entry.getKey(), -entry.getValue());
			}
			userEquipment.setInUsed(false);
			addMaterial(userEquipment);
		}
	}

	public void addMaterial(Map<Integer, Material> materialMap) {
		materialMap.putAll(materialMap);
	}

	public Map<Integer, UserEquipment> getEquipmentMap() {
		return equipmentMap;
	}

	public void setEquipmentMap(Map<Integer, UserEquipment> equipmentMap) {
		this.equipmentMap = equipmentMap;
	}

	public void decMaterial(int materialType, int id, int quantity) {
		Material material = materialMap.get(materialType).get(id);
		if (material.getQuantity() == 1) {
			materialMap.remove(id);
		} else {
			material.changeQuantity(-quantity);
		}
	}

	public Map<Integer, Map<Integer, Material>> getMaterialMap() {
		return materialMap;
	}

	public Material getMaterial(int materialType, int materialId) {
		return materialMap.get(materialType).get(materialId);
	}

	@Override
	public void notifyAttributeChange(Attribute attribute) {
		// TODO Auto-generated method stub
		Scene scene = SceneService.INSTANCE.getUserScene(userId);
		if (scene == null) {
			return;
		}
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFT_USER_ATTRIBUATE_CHANGE);
		replyDomain.setStringDomain("属性名称", attribute.getName());
		replyDomain.setIntDomain("value", attribute.getValue());
		replyDomain.setIntDomain("角色id", id);
		scene.notifyAllUser(replyDomain);
	}

	@Override
	public void afterBuffAdd(RoleBuff roleBuff) {
		// TODO Auto-generated method stub
		Scene scene = SceneService.INSTANCE.getUserScene(userId);
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_BUFF_ADD);
		replyDomain.setStringDomain("buff名称", roleBuff.getName());
		replyDomain.setIntDomain("角色id", id);
		scene.notifyAllUser(replyDomain);
	}

}
