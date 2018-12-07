package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.GainTreasureData;
import com.hh.mmorpg.result.ReplyDomain;

public class TreasureMaterialHandler extends AbstractMaterialHandler {

	@Override
	public ReplyDomain gainMaterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(materialStr[1]);
		int num = Integer.parseInt(materialStr[2]);

		UserTreasure treasure = role.getRoleTreasure(id);
		treasure.changeQuantity(num);

		GainTreasureData gainTreasureData = new GainTreasureData(role, id, num);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.TREASURE,
				new EventDealData<GainTreasureData>(gainTreasureData));

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain checkDecMaterial(Role role, String[] materialStr) {
		int id = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		UserTreasure treasure = role.getRoleTreasure(id);
		if (treasure == null) {
			return ReplyDomain.FAILE;
		}

		if (treasure.getQuantity() < needNum) {
			return ReplyDomain.NOT_ENOUGH;
		}

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] materialStr) {
		// TODO Auto-generated method stub
		int materialId = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		UserTreasure treasure = role.getRoleTreasure(materialId);

		treasure.changeQuantity(-needNum);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public int getPileNum(int materialId) {
		// TODO Auto-generated method stub
		return -1;
	}

	/**
	 * 财富类不能单独使用
	 */
	@Override
	public ReplyDomain useMaterial(Role role, int uniqueId) {
		// TODO Auto-generated method stub
		return null;
	}

}