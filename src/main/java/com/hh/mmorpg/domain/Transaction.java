package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.transaction.TransactionExtension;
import com.hh.mmorpg.service.user.UserService;

/**
 * 交易实体类
 * 
 * @author xhx
 *
 */
public class Transaction {

	private int id;
	private Role oneRole;
	private Role twoRole;

	// 交易中的物品不可堆叠，不然就会出现问题
	private Map<Integer, Map<Integer, Box>> exchangeMaterial;
	private Map<Integer, Map<Integer, Integer>> treasureMaterial;
	private Map<Integer, Boolean> roleConfirm;

	public Transaction(int id, Role oneRole, Role twoRole) {
		this.id = id;
		this.oneRole = oneRole;
		this.twoRole = twoRole;

		this.roleConfirm = new HashMap<>();
		roleConfirm.put(oneRole.getId(), false);
		roleConfirm.put(twoRole.getId(), false);

		this.exchangeMaterial = new HashMap<>();
		this.treasureMaterial = new HashMap<>();
	}

	/**
	 * 在交易中加入物品
	 * 
	 * @param roleId
	 * @param materialId
	 * @param num
	 */
	public void addMaterial(int roleId, BagMaterial bagMaterial) {
		Map<Integer, Box> map = exchangeMaterial.get(roleId);
		if (map == null) {
			map = new HashMap<>();
			exchangeMaterial.put(roleId, map);
		}
		Box box = new Box(bagMaterial);
		int size = map.size();
		map.put(size, box);
	}

	public void addTreasure(int roleId, int id, int num) {
		Map<Integer, Integer> map = treasureMaterial.get(roleId);
		if (map == null) {
			map = new HashMap<>();
			treasureMaterial.put(roleId, map);
		}

		int quantity = map.get(id) == null ? 0 : map.get(id);
		map.put(id, quantity + num);
	}

	public void confirm(int roleId) {
		roleConfirm.put(roleId, true);
	}

	/**
	 * 判断双方是否已经确认
	 * 
	 * @return
	 */
	public boolean judgeIsAllAccept() {
		for (Boolean isConfirm : roleConfirm.values()) {
			if (!isConfirm) {
				return false;
			}
		}
		return true;
	}

	public ReplyDomain startTrade() {

		List<BagMaterial> oneMaterial = getBagMaterialList(oneRole.getId());
		List<BagMaterial> twoMaterial = getBagMaterialList(twoRole.getId());

		// 获取金币
		Map<Integer, Integer> oneTreasure = treasureMaterial.get(oneRole.getId());
		Map<Integer, Integer> twoTreasure = treasureMaterial.get(twoRole.getId());

		// 检查双方物品栏是否足够位置，若不足，则立刻终止交易
		if (!oneRole.isMaterialBoxEnough(twoMaterial)) {
			ReplyDomain replyDomain = new ReplyDomain(ResultCode.FAILE);
			replyDomain.setStringDomain("失败原因", oneRole.getName() + "物品栏空间不足");
		}
		if (!twoRole.isMaterialBoxEnough(twoMaterial)) {
			ReplyDomain replyDomain = new ReplyDomain(ResultCode.FAILE);
			replyDomain.setStringDomain("失败原因", twoRole.getName() + "物品栏空间不足");
		}

		ReplyDomain oneReplyDomain = new ReplyDomain(ResultCode.SUCCESS);
		ReplyDomain twoReplyDomain = new ReplyDomain(ResultCode.SUCCESS);

		// 扣减物品 Start
		for (BagMaterial material : twoMaterial) {
			twoRole.decMaterialIndex(material.getIndex(), material.getQuantity());
			twoReplyDomain.setStringDomain("扣减物品", material.getName() + "*" + material.getQuantity());
		}
		for (BagMaterial material : oneMaterial) {
			oneRole.decMaterialIndex(material.getIndex(), material.getQuantity());
			oneReplyDomain.setStringDomain("扣减物品", material.getName() + "*" + material.getQuantity());
		}
		for (Entry<Integer, Integer> entry : oneTreasure.entrySet()) {
			UserTreasure treasure = oneRole.getRoleTreasure(entry.getKey());
			treasure.changeQuantity(-entry.getValue());
			oneReplyDomain.setStringDomain("扣减物品",
					UserTreasureType.getUserTreasureType(entry.getValue()) + "*" + entry.getValue());
		}
		for (Entry<Integer, Integer> entry : twoTreasure.entrySet()) {
			UserTreasure treasure = twoRole.getRoleTreasure(entry.getKey());
			treasure.changeQuantity(-entry.getValue());
			twoReplyDomain.setStringDomain("扣减物品",
					UserTreasureType.getUserTreasureType(entry.getValue()) + "*" + entry.getValue());
		}
		// 扣减物品 end
		
		// 获得物品
		for (BagMaterial material : twoMaterial) {
			oneRole.addMaterial(material);
			oneReplyDomain.setStringDomain("获得物品", material.getName() + "*" + material.getQuantity());
		}
		for (BagMaterial material : oneMaterial) {
			twoRole.addMaterial(material);
			twoReplyDomain.setStringDomain("获得物品", material.getName() + "*" + material.getQuantity());
		}

		for (Entry<Integer, Integer> entry : oneTreasure.entrySet()) {
			UserTreasure treasure = twoRole.getRoleTreasure(entry.getKey());
			treasure.changeQuantity(entry.getValue());
			twoReplyDomain.setStringDomain("获得物品",
					UserTreasureType.getUserTreasureType(entry.getValue()) + "*" + entry.getValue());
		}

		for (Entry<Integer, Integer> entry : twoTreasure.entrySet()) {
			UserTreasure treasure = oneRole.getRoleTreasure(entry.getKey());
			treasure.changeQuantity(entry.getValue());
			oneReplyDomain.setStringDomain("获得物品",
					UserTreasureType.getUserTreasureType(entry.getValue()) + "*" + entry.getValue());
		}

		// 发送交易成功的notify
		TransactionExtension.notifyRole(UserService.INSTANCE.getUser(oneRole.getUserId()), oneReplyDomain);
		TransactionExtension.notifyRole(UserService.INSTANCE.getUser(twoRole.getUserId()), twoReplyDomain);

		return ReplyDomain.SUCCESS;
	}

	private List<BagMaterial> getBagMaterialList(int roleId) {
		List<BagMaterial> list = new ArrayList<>();

		for (Box box : exchangeMaterial.get(roleId).values()) {
			list.add(box.getMaterial());
		}

		return list;
	}

	/*
	 * 获取另一个角色对象
	 */
	public Role getAnotherRole(int roleId) {
		// TODO Auto-generated method stub
		if (oneRole.getId() == roleId) {
			return twoRole;
		}
		return oneRole;
	}

	public Role getOneRole() {
		return oneRole;
	}

	public Role getTwoRole() {
		return twoRole;
	}

	public int getId() {
		return id;
	}

	// 交易物品格子类
	class Box {
		private BagMaterial material;

		public Box(BagMaterial bagMaterial) {
			this.material = bagMaterial;
		}

		public BagMaterial getMaterial() {
			return material;
		}

	}
}
