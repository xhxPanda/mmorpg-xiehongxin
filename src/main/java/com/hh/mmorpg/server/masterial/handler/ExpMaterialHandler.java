package com.hh.mmorpg.server.masterial.handler;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.MaterialExtension;

/**
 * 
 * @author xhx 处理用户经验
 */
public class ExpMaterialHandler extends AbstractMaterialHandler {

	private Map<Integer, Integer> levelLimitMap;

	public ExpMaterialHandler() {
		this.levelLimitMap = new HashMap<>();
		levelLimitMap.put(1, 0);
		levelLimitMap.put(2, 200);
		levelLimitMap.put(3, 400);
		levelLimitMap.put(4, 600);
		levelLimitMap.put(5, 800);
		levelLimitMap.put(6, 1000);
		levelLimitMap.put(7, 1200);
		levelLimitMap.put(8, 1400);
		levelLimitMap.put(9, 1600);
		levelLimitMap.put(10, 1800);
	}

	@Override
	public ReplyDomain gainMaterial(User user, Role role, String[] material) {

		int num = Integer.parseInt(material[2]);
		int level = role.getLevel();

		// 满级
		if (level == levelLimitMap.size()) {
			return ReplyDomain.SUCCESS;
		}

		int oldExp = role.getExp();

		int newExp = oldExp + num;

		int nextLevel = -1;
		for (int i = 1; i <= levelLimitMap.size(); i++) {
			if (newExp <= levelLimitMap.get(i)) {
				nextLevel = i;
				break;
			}
		}

		if (nextLevel == -1) {
			nextLevel = levelLimitMap.size();
		}

		if (nextLevel > level) {
			// 处理升级
			role.setLevel(nextLevel);

			// 判断是否满级
			newExp = (levelLimitMap.get(levelLimitMap.size()) - newExp) < 0 ? levelLimitMap.get(levelLimitMap.size())
					: newExp;

			// 抛出升级事件
			UpdateLevelData data = new UpdateLevelData(oldExp, nextLevel, role);
			EventHandler.INSTANCE.invodeMethod(UpdateLevelData.class, data);
		}

		role.setExp(newExp);

		ReplyDomain notify = new ReplyDomain(ResultCode.SUCCESS);
		notify.setStringDomain("cmd", "角色经验达到" + newExp);
		MaterialExtension.notifyMaterialGain(user, notify);

		return ReplyDomain.SUCCESS;
	}

	@Override
	public ReplyDomain decMasterial(User user, Role role, String[] material) {

		return ReplyDomain.FAILE;
	}

	@Override
	public int getPileNum(int level) {
		return 0;
	}

	@Override
	public ReplyDomain useMaterial(Role role, int uniqueId) {

		return null;
	}
}
