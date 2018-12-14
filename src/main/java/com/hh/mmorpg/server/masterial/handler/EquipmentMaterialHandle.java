package com.hh.mmorpg.server.masterial.handler;

import java.util.Map;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.EquimentDomain;
import com.hh.mmorpg.domain.MaterialType;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.GetMaterialData;
import com.hh.mmorpg.event.data.UserEquimentData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.equiment.UserEquimentService;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.MaterialExtension;
import com.hh.mmorpg.server.masterial.handler.xmlManager.EquimentXmlResolutionManager;

public class EquipmentMaterialHandle extends AbstractMaterialHandler {

	private Map<Integer, EquimentDomain> equimentDomainMap;

	public EquipmentMaterialHandle() {
		equimentDomainMap = EquimentXmlResolutionManager.INSTANCE.resolution();
	}

	/**
	 * 新添加装备
	 */
	@Override
	public ReplyDomain gainMaterial(User user, Role role, String[] materialStr) {
		int id = Integer.parseInt(materialStr[1]);
		EquimentDomain equimentDomain = equimentDomainMap.get(id);

		int roleId = role.getId();

		int uniqueId = IncrementManager.INSTANCE.increase("uniqueMaterial");

		// 在装备列表中新增一个新装备
		// 由于装备特殊，每新增一个装备都配备一个新的装备独立的id
		UserEquipment userEquipment = new UserEquipment(role.getId(), uniqueId, equimentDomain.getId(),
				equimentDomain.getName(), equimentDomain.getSellPrice(), equimentDomain.getEquimentLevel(),
				equimentDomain.getEquimentSource(), equimentDomain.getMaxDurability(),
				equimentDomain.getMaxDurability(), equimentDomain.getEquimentType(), equimentDomain.getAttributes(),
				false);

		// 持久化
		MaterialDao.INSTANCE.updateRoleEquiment(userEquipment);
		BagMaterial bagMaterial = new BagMaterial(uniqueId, roleId, equimentDomain.getId(), equimentDomain.getName(),
				MaterialType.EQUIPMENT_TYPE.getId(), 1, 0, equimentDomain.getSellPrice());
		ReplyDomain replyDomain = role.addMaterial(bagMaterial);

		if (replyDomain.isSuccess()) {
			UserEquimentService.INSTANCE.addEquiment(userEquipment);
		}

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("cmd", "新增物品" + equimentDomain.getName());
		MaterialExtension.notifyMaterialGain(user, notify);

		// 抛出获得物品的事件
		GetMaterialData data = new GetMaterialData(role, bagMaterial, 1);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.GET_MATERIAL, new EventDealData<GetMaterialData>(data));

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(User user, Role role, String[] materialStr) {
		return ReplyDomain.FAILE;
	}

	@Override
	public int getPileNum(int materialId) {
		return 1;
	}

	@Override
	public ReplyDomain useMaterial(Role role, int uniqueId) {
		UserEquipment equipment = UserEquimentService.INSTANCE.getUserEquiment(uniqueId);

		role.setEquipment(equipment);

		UserEquimentData userEquimentData = new UserEquimentData(role);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.WEAR_QUEIMENT,
				new EventDealData<UserEquimentData>(userEquimentData));
		return ReplyDomain.SUCCESS;
	}

}
