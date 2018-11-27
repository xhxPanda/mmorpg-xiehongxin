package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.GetMaterialData;
import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.MaterialService;
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

	private int tribeId; // 种族id
	private int occupationId; // 职业id

	private int capacity; // 背包容量
	private int level; // 等级
	private int exp; // 经验
	private int lastJoinScene; // 掉线前所在的场景

	private int guildId; // 公会id
	private int teamId;

	private int transactionPerson; // 交易状态

	private Map<Integer, Map<Integer, RoleMission>> roleMissionCache;

	// 背包
	private Map<Integer, BagMaterial> materialMap;
	// 财富背包
	private Map<Integer, UserTreasure> treasureMap;
	// 装备栏
	private Map<Integer, UserEquipment> equipmentMap;

	public Role(int userId, int id, String name, int tribeId, int occupationId, int capacity, int level, int exp,
			int lastJoinScene, int teamId) {
		super(tribeId, id);
		this.userId = userId;
		this.id = id;
		this.name = name;
		this.occupationId = occupationId;
		this.capacity = capacity;
		this.materialMap = new HashMap<>();

		this.level = level;
		this.exp = exp;
		this.lastJoinScene = lastJoinScene;

		for (int i = 0; i < capacity; i++) {
			materialMap.put(i, null);
		}
		this.equipmentMap = new HashMap<>();

		this.treasureMap = new HashMap<>();
		for (UserTreasureType type : UserTreasureType.values()) {
			treasureMap.put(type.getId(), new UserTreasure(id, type.getName(), type.getId(), 0));
		}

		this.transactionPerson = 0;
		this.teamId = teamId;
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

	public int getCapacity() {
		return capacity;
	}

	public static final ResultBuilder<Role> BUILDER = new ResultBuilder<Role>() {

		@Override
		public Role build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int userId = result.getInt(1);
			int id = result.getInt(2);
			String name = result.getString("name");
			int capacity = result.getInt("capacity");
			int level = result.getInt("level");
			int exp = result.getInt("exp");
			int tribeId = result.getInt("tribeId");
			int occupationId = result.getInt("occupationId");
			int lastJoinScene = result.getInt("lastJoinScene");
			int teamId = result.getInt("teamId");
			return new Role(userId, id, name, tribeId, occupationId, capacity, level, exp, lastJoinScene, teamId);
		}
	};

	@Override
	public String toString() {
		return "Role [userId=" + userId + ", id=" + id + ", name=" + name + ", 职业=" + occupationId + ", hp="
				+ getAttribute(3).getValue() + ", mp=" + getAttribute(4).getValue() + ", 攻击力="
				+ getAttribute(1).getValue() + ", 防御力=" + getAttribute(2).getValue() + "]";
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub

	}

	/**
	 * 角色初始化时把物品放进格子之中
	 * 
	 * @param material
	 */
	public void pushMaterial(BagMaterial material) {
		materialMap.put(material.getIndex(), material);
	}

	public ReplyDomain addMaterial(BagMaterial material) {

		int pileNum = MaterialService.INSTANCE.getMaterialPileNum(material.getType(), material.getId());

		List<BagMaterial> materials = getMaterialById(material.getId());

		int addNum = 0;
		// 旧格子(可堆叠的情况下
		if (materials.size() > 0) {
			for (BagMaterial m : materials) {
				if (m.getQuantity() + material.getQuantity() > pileNum) {
					int realAddNum = pileNum - m.getQuantity();
					if (realAddNum <= 0) {
						continue;
					}

					m.changeQuantity(realAddNum);
					m.setQuantity(m.getQuantity() - realAddNum);
					addNum += realAddNum;
				}
			}
		}

		while (material.getQuantity() > 0) {
			// 寻找新的格子
			int index = findFreeBox();
			if (index != -1) {
				materialMap.put(index, material);
				material.setIndex(index);
				material.setRoleId(id);
				addNum += material.getQuantity();
			} else {

				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("不能成功添加的物品", material.getName());
				replyDomain.setIntDomain("数量为", material.getQuantity());
				return ReplyDomain.BOX_SPACE_NOT_ENOUGH;
			}
		}
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setIntDomain("真实插入的数量", addNum);

		// 抛出获得物品的事件
		if (addNum > 0) {
			GetMaterialData data = new GetMaterialData(this, material, addNum);
			EventHandlerManager.INSATNCE.methodInvoke(EventType.TALK_TO_NPC, new EventDealData<GetMaterialData>(data));
		}

		return replyDomain;
	}

	/**
	 * 穿上装备
	 * 
	 * @param equimentId
	 */
	public void setEquipment(UserEquipment equipment) {
		int equipmentType = equipment.getEquimentType();
		if (equipmentMap.size() != 0 && (equipmentMap.get(equipmentType) != null
				&& equipmentMap.get(equipmentType).getId() == equipment.getId())) {
			return;
		}

		takeOffEquiment(equipmentType);

		// 装备服装
		equipmentMap.put(equipment.getEquimentType(), equipment);
		for (Entry<Integer, Integer> entry : equipment.getAttributeMap().entrySet()) {
			effectAttribute(entry.getKey(), entry.getValue(), "穿上装备");
		}
		equipment.setInUsed(true);
		if (!equipment.isInUsed()) {
			MaterialDao.INSTANCE.updateRoleEquiment(equipment);
		}

	}

	/**
	 * 卸下装备
	 * 
	 * @param equipmentType
	 */
	public void takeOffEquiment(int equipmentType) {
		if (equipmentMap.containsKey(equipmentType)) {
			UserEquipment userEquipment = equipmentMap.remove(equipmentType);

			// 卸下服装
			for (Entry<Integer, Integer> entry : userEquipment.getAttributeMap().entrySet()) {
				effectAttribute(entry.getKey(), -entry.getValue(), "卸下装备");
			}
			userEquipment.setInUsed(false);
			addMaterial(userEquipment);
		}
	}

//	private int getEquimentSource() {
//		int totalSource = 0;
//		for (UserEquipment equipment : equipmentMap.values()) {
//			totalSource += equipment.getEquimentSource();
//		}
//		return totalSource;
//	}

	public void addMaterial(Map<Integer, Material> materialMap) {
		materialMap.putAll(materialMap);
	}

	/**
	 * 减少背包中某一种物品，使用物品id进行查询
	 * 
	 * @param materialType
	 * @param id
	 * @param quantity
	 * @return
	 */
	public ReplyDomain decMaterial(int materialType, int id, int quantity) {
		List<BagMaterial> materials = getMaterialById(id);

		int totalNum = 0;
		for (BagMaterial m : materials) {
			totalNum += m.getQuantity();
		}

		if (totalNum < quantity)
			return ReplyDomain.NOT_ENOUGH;

		for (BagMaterial m : materials) {
			if (m.getQuantity() >= quantity) {
				m.changeQuantity(-quantity);
			} else {
				m.changeQuantity(-(quantity - m.getQuantity()));
				quantity -= m.getQuantity();
			}

			// 这个位置变为空
			if (m.getQuantity() == 0) {
				materialMap.put(m.getIndex(), null);
				// 从数据库中删除
				MaterialDao.INSTANCE.deleteMaterial(m.getType(), m.getId(), m.getIndex());
			}
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 减少某个格子中的物品数量
	 * 
	 * @param index
	 * @param num
	 * @return
	 */
	public Material decMaterialIndex(int index, int num) {
		BagMaterial material = materialMap.get(index);

		if (material == null || material.getQuantity() == 0) {
			return null;
		}

		material.changeQuantity(num);
		if (material.getQuantity() == 0) {
			materialMap.put(material.getIndex(), null);
		}

		return material;
	}

	public int findFreeBox() {
		for (Entry<Integer, BagMaterial> entry : materialMap.entrySet()) {
			if (entry.getValue() == null) {
				return entry.getKey();
			}
		}

		return -1;
	}

	/**
	 * 使用物品id获取物品
	 * 
	 * @param materialId
	 * @return
	 */
	public List<BagMaterial> getMaterialById(int materialId) {
		List<BagMaterial> materials = new ArrayList<>();
		for (Entry<Integer, BagMaterial> entry : materialMap.entrySet()) {
			if (entry.getValue() != null && entry.getValue().getId() == materialId) {
				materials.add(entry.getValue());
			}

		}

		return materials;
	}

	/**
	 * 使用财富id获取财富类的物品
	 * 
	 * @param id
	 * @return
	 */
	public UserTreasure getRoleTreasure(int id) {
		return treasureMap.get(id);
	}

	/**
	 * 整理背包
	 */
	public void arrangeBag() {

		// 拷贝出副本
		Map<Integer, BagMaterial> copyMap = new HashMap<>();
		for (int i = 0; i < capacity; i++) {
			copyMap.put(i, null);
		}
		// 根据materialType进行排序，把一样类型的物品进行归类
		TreeMap<Integer, List<BagMaterial>> sortMap = new TreeMap<>(new Comparator<Integer>() {
			public int compare(Integer obj1, Integer obj2) {
				// 根据物品类型升序排序
				return obj1.compareTo(obj2);
			}
		});

		for (BagMaterial bagMaterial : materialMap.values()) {
			if (bagMaterial != null) {
				List<BagMaterial> materialList = sortMap.get(bagMaterial.getType());
				if (materialList == null) {
					materialList = new ArrayList<>();
					sortMap.put(bagMaterial.getType(), materialList);
				}

				materialList.add(bagMaterial);
			}
		}

		// 对副本进行插入整理
		int index = 0;
		for (List<BagMaterial> bagMaterialList : sortMap.values()) {
			if (bagMaterialList.size() == 0) {
				continue;
			}

			for (BagMaterial bagMaterial : bagMaterialList) {
				copyMap.put(index, bagMaterial);
				bagMaterial.setIndex(index);
				index++;
			}
		}

		// 副本复制给正本
		materialMap = copyMap;

	}

	/**
	 * 物品排序
	 * 
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public ReplyDomain sortBag(int fromIndex, int toIndex) {
		BagMaterial fromMaterial = materialMap.get(fromIndex);
		if (fromMaterial == null) {
			return ReplyDomain.FAILE;
		}

		BagMaterial toMaterial = materialMap.get(toIndex);
		materialMap.put(fromIndex, toMaterial);
		materialMap.put(toIndex, fromMaterial);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 使用materialId判断该物品是否足够
	 * 
	 * @param materialId
	 * @param num
	 * @return
	 */
	public boolean isMaterialEnough(int materialId, int num) {
		// TODO Auto-generated method stub

		if (materialId < 100001) {
			return getRoleTreasure(materialId).getQuantity() > num;
		}
		List<BagMaterial> materials = getMaterialById(materialId);
		int totalNum = 0;
		for (BagMaterial bagMaterial : materials) {
			totalNum += bagMaterial.getQuantity();
		}

		return totalNum > num;
	}

	/**
	 * 当要插入一堆物品的时候，查看用户的背包是否足够，保证事务的正确
	 * 
	 * @param bagMaterials
	 * @return
	 */
	public boolean isMaterialBoxEnough(List<BagMaterial> bagMaterials) {

		// 获取当前空闲的格子数
		int freeBoxNum = getFreeBoxNum();

		for (BagMaterial bagMaterial : bagMaterials) {
			List<BagMaterial> ownBagMaterials = getMaterialById(bagMaterial.getId());

			// 查看堆叠个数
			int pileNum = MaterialService.INSTANCE.getMaterialPileNum(bagMaterial.getType(), bagMaterial.getId());
			for (BagMaterial material : ownBagMaterials) {
				if (material.getQuantity() + bagMaterial.getQuantity() > pileNum) {
					freeBoxNum--;
					if (freeBoxNum == 0) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * 获取当前没装东西的格子的数量
	 * 
	 * @return
	 */
	private int getFreeBoxNum() {
		int total = 0;

		for (BagMaterial bagMaterial : materialMap.values()) {
			if (bagMaterial == null) {
				total += 1;
			}
		}
		return total;
	}

	/**
	 * 获取格子中的物品
	 * 
	 * @param index
	 * @return
	 */
	public BagMaterial getBagMaterialIndex(int index) {
		return materialMap.get(index);
	}

	/**
	 * 获取用户已接受的任务
	 * 
	 * @param type
	 * @param missionId
	 * @return
	 */
	public RoleMission getMission(int type, int missionId) {
		return roleMissionCache.get(type).get(missionId);
	}

	public void reciveMission(RoleMission roleMission) {
		Map<Integer, RoleMission> roleMissionMap = roleMissionCache.get(roleMission.getType());
		if (roleMissionMap == null) {
			roleMissionMap = new HashMap<>();
			roleMissionCache.put(roleMission.getType(), roleMissionMap);
		}

		roleMissionMap.put(roleMission.getMissionId(), roleMission);
	}

	/**
	 * 学习新的技能
	 * 
	 * @param roleSkill
	 */
	public void learnNewSkill(RoleSkill roleSkill) {
		getSkillMap().put(roleSkill.getSkillId(), roleSkill);
	}

	@Override
	public void notifyAttributeChange(Attribute attribute, String reason) {
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
		if (scene != null) {
			ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
			replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_BUFF_ADD);
			replyDomain.setStringDomain("buff名称", roleBuff.getName());
			replyDomain.setIntDomain("角色id", id);
			scene.notifyAllUser(replyDomain);
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	public Map<Integer, BagMaterial> getMaterialMap() {
		return materialMap;
	}

	public Map<Integer, UserTreasure> getTreasureMap() {
		return treasureMap;
	}

	public int getGuildId() {
		return guildId;
	}

	public void setGuildId(int guildId) {
		this.guildId = guildId;
	}

	public int getTransactionPerson() {
		return transactionPerson;
	}

	public void setTransactionPerson(int transactionPerson) {
		this.transactionPerson = transactionPerson;
	}

	public Map<Integer, UserEquipment> getEquipmentMap() {
		return equipmentMap;
	}

	public void setEquipmentMap(Map<Integer, UserEquipment> equipmentMap) {
		this.equipmentMap = equipmentMap;
	}

	public Map<Integer, Map<Integer, RoleMission>> getRoleMissionCache() {
		return roleMissionCache;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getOccupationId() {
		return occupationId;
	}

	public void setOccupationId(int occupationId) {
		this.occupationId = occupationId;
	}

	public int getTribeId() {
		return tribeId;
	}

	public int getLastJoinScene() {
		return lastJoinScene;
	}

	public void setLastJoinScene(int lastJoinScene) {
		this.lastJoinScene = lastJoinScene;
	}

}
