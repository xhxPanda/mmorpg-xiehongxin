package com.hh.mmorpg.domain;

import java.util.List;

public class SummonMonster extends LivingThing {

	private LivingThing belonger;
	private long terminalTime;

	// 单体攻击主攻对象
	private List<? extends LivingThing> targetAttackObject;

	public SummonMonster(LivingThing belonger, int uniqueId, int sceneId, SummonDomain domain, long insistTime,
			List<? extends LivingThing> targetAttackObject, LivingThing targetObject) {
		super(domain.getId(), uniqueId, domain.getName());
		this.belonger = belonger;
		this.terminalTime = insistTime + System.currentTimeMillis();
		this.targetAttackObject = targetAttackObject;

		setAttributeMap(domain.getAttributeMap());
		setSkillMap(domain.getSkillMap());
		setSceneId(sceneId);
		setAttackObject(targetObject);
	}

	@Override
	public void afterDead() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAttributeChange(Attribute attribute, String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterBuffAdd(RoleBuff roleBuff) {
		// TODO Auto-generated method stub

	}

	public LivingThing getBelonger() {
		return belonger;
	}

	public long getTerminalTime() {
		return terminalTime;
	}

	public List<? extends LivingThing> getTargetAttackObject() {
		return targetAttackObject;
	}

}
