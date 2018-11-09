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

	private String name;
	private int freshTime;
	private int sceneId;
	private int attackRoleId;

	private Map<String, Integer> killFallItemMap;

	public Monster(int uniqueId, int sceneId, MonsterDomain domain) {
		super(domain.getId(), uniqueId);
		this.name = domain.getName();
		this.freshTime = domain.getFreshTime();
		this.sceneId = sceneId;
		this.killFallItemMap = domain.getKillFallItemMap();
		setAttributeMap(domain.getAttributeMap());
		setSkillMap(domain.getRoleSkillMap());
	}

	public int getFreshTime() {
		return freshTime;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Monster [名称=" + name + ", id=" + getUniqueId() + ", hp=" + getAttribute(3).getValue() + ", mp="
				+ getAttribute(4).getValue() + " ]";
	}

	public synchronized void setAttackRole(int roleId) {
		if (attackRoleId == 0) {
			this.attackRoleId = roleId;
		}
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub
		setStatus(false);
		setBeKilledTime(System.currentTimeMillis());

		MonsterDeadData data = new MonsterDeadData(getUniqueId(), attackRoleId, getSceneId());
		EventHandlerManager.INSATNCE.methodInvoke(EventType.MONSTER_DEAD, new EventDealData<MonsterDeadData>(data));
	}

	public void setSceneId(int sceneId) {
		this.sceneId = sceneId;
	}

	public int getSceneId() {
		return sceneId;
	}

	public int getAttackRoleId() {
		return attackRoleId;
	}

	public Map<String, Integer> getKillFallItemMap() {
		return killFallItemMap;
	}

	public void setKillFallItemMap(Map<String, Integer> killFallItemMap) {
		this.killFallItemMap = killFallItemMap;
	}

	@Override
	public void notifyAttributeChange(Attribute attribute, String reason) {
		// TODO Auto-generated method stub
		Scene scene = SceneService.INSTANCE.getSceneMap().get(sceneId);
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
		Scene scene = SceneService.INSTANCE.getSceneMap().get(sceneId);
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_BUFF_ADD);
		replyDomain.setStringDomain("buff名称", roleBuff.getName());
		replyDomain.setIntDomain("monsterId", getUniqueId());
		replyDomain.setStringDomain("怪物名称", getName());
		scene.notifyAllUser(replyDomain);
	}
}
