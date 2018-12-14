package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.GainTreasureData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.MaterialExtension;

public class TreasureMaterialHandler extends AbstractMaterialHandler {

	@Override
	public ReplyDomain gainMaterial(User user, Role role, String[] materialStr) {

		int id = Integer.parseInt(materialStr[1]);
		int num = Integer.parseInt(materialStr[2]);

		UserTreasure treasure = role.getRoleTreasure(id);
		treasure.changeQuantity(num);

		GainTreasureData gainTreasureData = new GainTreasureData(role, id, num);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.TREASURE,
				new EventDealData<GainTreasureData>(gainTreasureData));

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("cmd", "新增" + num + treasure.getName());
		MaterialExtension.notifyMaterialGain(user, notify);

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
	public ReplyDomain decMasterial(User user, Role role, String[] materialStr) {

		int materialId = Integer.parseInt(materialStr[1]);
		int needNum = Integer.parseInt(materialStr[2]);

		UserTreasure treasure = role.getRoleTreasure(materialId);

		treasure.changeQuantity(-needNum);

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("cmd", "减少" + needNum + treasure.getName());
		MaterialExtension.notifyMaterialDec(user, notify);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public int getPileNum(int materialId) {

		return -1;
	}

	/**
	 * 财富类不能单独使用
	 */
	@Override
	public ReplyDomain useMaterial(Role role, int uniqueId) {

		return null;
	}

}