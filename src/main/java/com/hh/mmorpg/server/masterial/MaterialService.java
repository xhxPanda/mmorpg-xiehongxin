package com.hh.mmorpg.server.masterial;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Goods;
import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.handler.AbstractMaterialHandler;
import com.hh.mmorpg.server.masterial.handler.EquipmentMaterialHandle;
import com.hh.mmorpg.server.masterial.handler.ItemMasterialHandler;
import com.hh.mmorpg.server.masterial.handler.TreasureMaterialHandler;
import com.hh.mmorpg.server.masterial.handler.xmlManager.GoodsXmlResolutionManager;
import com.hh.mmorpg.server.role.RoleService;

/**
 * 这个类主要处理的是有关用户新增物品（由String组成的物品代号）进行的material插入
 * 
 * @author xhx
 * 
 */
public class MaterialService {

	public static final MaterialService INSTANCE = new MaterialService();

	private Map<Integer, AbstractMaterialHandler> handlerMap;
	private Map<Integer, Goods> goodsMap;

	private MaterialService() {
		// 配置material不同的handler
		this.handlerMap = new HashMap<>();
		this.handlerMap.put(MaterialType.EQUIPMENT_TYPE.getId(), new EquipmentMaterialHandle());
		this.handlerMap.put(MaterialType.ITEM_TYPE.getId(), new ItemMasterialHandler());
		this.handlerMap.put(MaterialType.TREASURE_TYPE.getId(), new TreasureMaterialHandler());
		this.handlerMap.put(MaterialType.TREASURE_TYPE.getId(), new TreasureMaterialHandler());

		goodsMap = GoodsXmlResolutionManager.INSTANCE.resolution();
	}

	public ReplyDomain showGoods(User user) {
		// TODO Auto-generated method stub
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setListDomain("商品列表", goodsMap.values());
		return replyDomain;
	}

	public ReplyDomain showAllMaterial(User user) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		ReplyDomain replyDomain = new ReplyDomain();

		StringBuilder bagMaterialStr = new StringBuilder();
		for (Entry<Integer, BagMaterial> materialEntry : role.getMaterialMap().entrySet()) {
			if (bagMaterialStr.length() > 0) {
				bagMaterialStr.append(";");
			}
			bagMaterialStr.append(materialEntry.getKey()).append(":");
			if (materialEntry.getValue() == null) {
				bagMaterialStr.append("空");
			} else {
				bagMaterialStr.append("(").append("名称：").append(materialEntry.getValue().getName()).append(",")
						.append("类型：")
						.append(MaterialType.getMaterialType(materialEntry.getValue().getTypeId()).getName())
						.append("数量：").append(materialEntry.getValue().getQuantity()).append(")");
			}

		}
		replyDomain.setStringDomain("物品列表", bagMaterialStr.toString());

		StringBuilder treasureMaterialStr = new StringBuilder();
		for (Entry<Integer, UserTreasure> materialEntry : role.getTreasureMap().entrySet()) {
			if (bagMaterialStr.length() > 0) {
				bagMaterialStr.append("|");
			}
			treasureMaterialStr.append("(").append("名称：").append(materialEntry.getValue().getName()).append(",")
					.append("类型：").append(MaterialType.TREASURE_TYPE.getName()).append("数量：")
					.append(materialEntry.getValue().getQuantity()).append(")");
		}
		replyDomain.setStringDomain("钱币列表", treasureMaterialStr.toString());
		return replyDomain;
	}

	public ReplyDomain buyGoods(User user, int goodsId, int num) {
		// TODO Auto-generated method stub
		if (num < 0) {
			return ReplyDomain.FAILE;
		}

		Goods goods = goodsMap.get(goodsId);

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		String price = concatenationGoods(goods.getPrice(), num);

		ReplyDomain decResult = decMasterial(user, role, price);
		if (!decResult.isSuccess()) {
			return decResult;
		}

		String item = concatenationGoods(goods.getItem(), num);
		ReplyDomain gainResult = gainMasteral(user, role, item);
		if (!gainResult.isSuccess()) {
			return gainResult;
		}

		return ReplyDomain.SUCCESS;
	}

	private String concatenationGoods(String materialStr, int num) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < num; i++) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append("#");
			}
			stringBuilder.append(materialStr);
		}

		return stringBuilder.toString();
	}

	public ReplyDomain gainMasteral(User user, Role role, String material) {

		String strs[] = material.split("#");
		ReplyDomain replyDomain = null;
		for (String s : strs) {
			String materialList[] = s.split(":");
			int type = Integer.parseInt(materialList[0]);
			if (handlerMap.get(type) == null) {
				return ReplyDomain.FAILE;
			}

			replyDomain = handlerMap.get(type).gainMaterial(role, materialList);
			if (!replyDomain.isSuccess()) {
				replyDomain.setStringDomain("worngMaterial", s);
				return replyDomain;
			}

		}

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("m", material);
		MaterialExtension.notifyMaterialGain(user, notify);
		return replyDomain;
	}

	public ReplyDomain decMasterial(User user, Role role, String material) {
		String[] materials = material.split("#");
		ReplyDomain checkReply = checkDecMaterial(role, material);
		if (!checkReply.isSuccess()) {
			return checkReply;
		}

		// 真实删除物品的流程
		for (String m : materials) {
			String strs[] = m.split(":");
			int type = Integer.parseInt(strs[0]);
			if (handlerMap.get(type) == null) {
				return ReplyDomain.FAILE;
			}
			handlerMap.get(type).decMasterial(role, strs);
		}

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("m", material);
		MaterialExtension.notifyMaterialDec(user, notify);

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain checkDecMaterial(Role role, String material) {
		String[] materials = material.split("#");

		// 检查是否能够扣除所有物品，进行事务处理
		for (String m : materials) {
			String strs[] = m.split(":");
			int type = Integer.parseInt(strs[0]);
			ReplyDomain replyDomain = handlerMap.get(type).checkDecMaterial(role, strs);
			if (!replyDomain.isSuccess()) {
				return replyDomain;
			}
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain useMaterial(User user, int index) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		BagMaterial material = role.decMaterialIndex(index, 1);
		if (material == null) {
			return ReplyDomain.FAILE;
		}

		return handlerMap.get(material.getTypeId()).useMaterial(role, material.getUniqueId());
	}

	public ReplyDomain sellGoods(User user, String materialStr) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		String strs[] = materialStr.split(":");
		int type = Integer.parseInt(strs[0]);
		int materialId = Integer.parseInt(strs[1]);
		int num = Integer.parseInt(strs[2]);

		if (type == MaterialType.TREASURE_TYPE.getId()) {
			return ReplyDomain.FAILE;
		}

		BagMaterial bagMaterial = role.getMaterialById(materialId).get(0);

		ReplyDomain replyDomain = decMasterial(user, role, materialStr);
		if (!replyDomain.isSuccess()) {
			return replyDomain;
		}

		for (int i = 0; i < num; i++) {
			gainMasteral(user, role, bagMaterial.getSellPrice());
		}

		return ReplyDomain.SUCCESS;
	}

	public int getMaterialPileNum(int type, int materialId) {
		return handlerMap.get(type).getPileNum(materialId);
	}

	// 用户切换角色后将角色物品持久化
	@Event(eventType = EventType.ROLE_CHANGE)
	public void handleRoleChange(EventDealData<RoleChangeData> data) {

		Role role = data.getData().getOldRole();
		persistenceRoleMatetrial(role);
	}

	/**
	 * 用户下线后统一把物品持久化
	 * 
	 * @param role
	 */
	public void persistenceRoleMatetrial(Role role) {
		for (BagMaterial bagMaterial : role.getMaterialMap().values()) {
			if (bagMaterial == null) {
				continue;
			}
			MaterialDao.INSTANCE.updateRoleMaterial(bagMaterial);
		}

		for (UserTreasure treasure : role.getTreasureMap().values()) {
			if (treasure == null) {
				continue;
			}
			MaterialDao.INSTANCE.updateRoleTreasure(treasure);
		}
	}

	/**
	 * 整理背包
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain arrangeBag(User user) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		role.arrangeBag();
		return showAllMaterial(user);
	}

	/**
	 * 背包排序
	 * 
	 * @param user
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public ReplyDomain sortBag(User user, int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		return role.sortBag(fromIndex, toIndex);
	}
}
