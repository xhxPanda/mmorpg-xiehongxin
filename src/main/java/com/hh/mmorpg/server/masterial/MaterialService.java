package com.hh.mmorpg.server.masterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.domain.Goods;
import com.hh.mmorpg.domain.Material;
import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.handler.AbstractMaterialHandler;
import com.hh.mmorpg.server.masterial.handler.EquipmentMaterialHandle;
import com.hh.mmorpg.server.masterial.handler.ItemMasterialHandler;
import com.hh.mmorpg.server.masterial.handler.TreasureMaterialHandler;
import com.hh.mmorpg.server.masterial.handler.xmlManager.GoodsXmlResolutionManager;
import com.hh.mmorpg.server.role.RoleService;

/**
 * 
 * @author xhx
 * 
 */
public class MaterialService {

	public static final MaterialService INSTANCE = new MaterialService();

	@SuppressWarnings("rawtypes")
	private Map<Integer, AbstractMaterialHandler> handlerMap;
	private Map<Integer, Goods> goodsMap;

	private MaterialService() {
		// 配置material不同的handler
		this.handlerMap = new HashMap<>();
		handlerMap.put(MaterialType.EQUIPMENT_TYPE.getId(), new EquipmentMaterialHandle());
		handlerMap.put(MaterialType.ITEM_TYPE.getId(), new ItemMasterialHandler());
		handlerMap.put(MaterialType.TREASURE_TYPE.getId(), new TreasureMaterialHandler());

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
		
		List<Material> materials = new ArrayList<Material>();
		for (Entry<Integer, Map<Integer, Material>> materialEntry : role.getMaterialMap().entrySet()) {
			materials.addAll(materialEntry.getValue().values());
		}
		replyDomain.setListDomain("物品列表", materials);
		return replyDomain;
	}

	public ReplyDomain buyGoods(User user, int goodsId, int num) {
		// TODO Auto-generated method stub
		if (num < 0) {
			return ReplyDomain.FAILE;
		}

		Goods goods = goodsMap.get(goodsId);

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		ReplyDomain decResult = decMasterial(user, role, goods.getPrice());
		if (!decResult.isSuccess()) {
			return decResult;
		}

		ReplyDomain gainResult = gainMasteral(user, role, goods.getItem());
		if (!gainResult.isSuccess()) {
			return gainResult;
		}

		return ReplyDomain.SUCCESS;
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

	@SuppressWarnings("unchecked")
	public void persistenceMatetrial(Material material) {
		int type = material.getType();
		handlerMap.get(type).persistence(material);
	}

	public ReplyDomain sellGoods(User user, String materialStr) {
		// TODO Auto-generated method stub
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		String strs[] = materialStr.split(":");
		int type = Integer.parseInt(strs[0]);
		int materialId = Integer.parseInt(strs[1]);

		if (type == MaterialType.TREASURE_TYPE.getId()) {
			return ReplyDomain.FAILE;
		}

		Material material = role.getMaterial(type, materialId);
		decMasterial(user, role, materialStr);

		gainMasteral(user, role, material.getSellPrice());
		return ReplyDomain.SUCCESS;
	}

}
