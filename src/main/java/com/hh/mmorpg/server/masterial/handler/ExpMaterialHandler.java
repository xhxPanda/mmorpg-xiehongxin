package com.hh.mmorpg.server.masterial.handler;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.Exp;
import com.hh.mmorpg.domain.LevelLimitDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.result.ReplyDomain;

/**
 * 
 * @author xhx 处理用户经验
 */
public class ExpMaterialHandler extends AbstractMaterialHandler<Exp> {

	private Map<Integer, LevelLimitDomain> levelLimitMap;

	public ExpMaterialHandler() {
		this.levelLimitMap = new HashMap<>();
	}

	@Override
	public ReplyDomain gainMaterial(Role role, String[] material) {
		// TODO Auto-generated method stub

		int num = Integer.parseInt(material[2]);
		int level = role.getLevel();

		// 满级
		if (level == levelLimitMap.size()) {
			return ReplyDomain.SUCCESS;
		}

		int oldExp = role.getExp();

		int newExp = oldExp + num;

		int nextLevel = -1;
		for (LevelLimitDomain domain : levelLimitMap.values()) {
			if (newExp < domain.getNeedExp()) {
				nextLevel = domain.getLevel();
			}
		}

		if (nextLevel == level) {
			role.setExp(newExp);
		} else if (nextLevel > level) {

			// 处理升级
			role.setLevel(nextLevel);

			// 判断是否满级
			newExp = (levelLimitMap.get(levelLimitMap.size()).getNeedExp() - newExp) < 0
					? levelLimitMap.get(levelLimitMap.size()).getNeedExp()
					: newExp;
			role.setExp(newExp);

			// 抛出升级事件
			UpdateLevelData data = new UpdateLevelData(oldExp, nextLevel, role);
			EventHandlerManager.INSATNCE.methodInvoke(EventType.LEVEL_UP, new EventDealData<UpdateLevelData>(data));
		}

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(Role role, String[] material) {
		// TODO Auto-generated method stub
		return ReplyDomain.FAILE;
	}

	@Override
	public int getPileNum(int level) {
		// TODO Auto-generated method stub
		if (level == levelLimitMap.size()) {
			return levelLimitMap.get(level).getNeedExp();
		}
		return levelLimitMap.get(level + 1).getNeedExp();
	}

}
