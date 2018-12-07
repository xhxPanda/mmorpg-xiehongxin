package com.hh.mmorpg.domain;

import java.util.Map;

import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.scene.SceneExtension;
import com.hh.mmorpg.server.scene.SceneService;

public class Monster extends LivingThing {

	private int freshTime;

	private Map<String, Integer> killFallItemMap;

	private boolean isNeedAi; // 是否需要主动攻击
	private int exp; // 经验

	public Monster(int uniqueId, int sceneId, MonsterDomain domain, boolean isNeedAi) {
		super(domain.getId(), uniqueId, domain.getName());

		this.freshTime = domain.getFreshTime();
		this.killFallItemMap = domain.getKillFallItemMap();
		this.isNeedAi = isNeedAi;
		this.exp = domain.getExp();
		setAttributeMap(domain.getAttributeMap());
		setSkillMap(domain.getRoleSkillMap());
		setSceneId(sceneId);
	}

	public int getFreshTime() {
		return freshTime;
	}

	@Override
	public String toString() {
		return "Monster [名称=" + getName() + ", id=" + getUniqueId() + ", hp=" + getAttribute(3).getValue() + ", mp="
				+ getAttribute(4).getValue() + " ]";
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub
		setStatus(false);
		setBeKilledTime(System.currentTimeMillis());

		// 抛出最后一击的人
		MonsterDeadData data = new MonsterDeadData(this, (Role) getAttackObject(), getSceneId());
		EventHandlerManager.INSATNCE.methodInvoke(EventType.MONSTER_DEAD, new EventDealData<MonsterDeadData>(data));

		setAttackObject(null);
	}

	public Map<String, Integer> getKillFallItemMap() {
		return killFallItemMap;
	}

	public void setKillFallItemMap(Map<String, Integer> killFallItemMap) {
		this.killFallItemMap = killFallItemMap;
	}

	public boolean isNeedAi() {
		return isNeedAi;
	}

	public int getExp() {
		return exp;
	}

	@Override
	public void notifyAttributeChange(Attribute attribute, String reason) {
		// TODO Auto-generated method stub
		Scene scene = SceneService.INSTANCE.getSceneMap().get(getSceneId());
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_ATTRIBUATE_CHANGE);
		replyDomain.setIntDomain("变化后的" + attribute.getName(), attribute.getValue());
		replyDomain.setIntDomain("怪物id", getUniqueId());
		replyDomain.setStringDomain("怪物名称", getName());
		replyDomain.setStringDomain("怪物名称", getName());
		scene.notifyAllUser(replyDomain);
	}

	@Override
	public void afterBuffAdd(RoleBuff roleBuff) {
		// TODO Auto-generated method stub
		Scene scene = SceneService.INSTANCE.getSceneMap().get(getSceneId());
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_BUFF_ADD);
		replyDomain.setStringDomain("buff名称", roleBuff.getName());
		replyDomain.setIntDomain("monsterId", getUniqueId());
		replyDomain.setStringDomain("怪物名称", getName());
		scene.notifyAllUser(replyDomain);
	}
}
