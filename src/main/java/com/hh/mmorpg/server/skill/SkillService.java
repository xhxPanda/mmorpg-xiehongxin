package com.hh.mmorpg.server.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.hh.mmorpg.domain.BuffDomain;
import com.hh.mmorpg.domain.LivingThing;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleBuff;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.SkillDomain;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.skill.xmlResolution.SkillXmlResolution;

public class SkillService {

	public static final SkillService INSTANCE = new SkillService();

	private Map<Integer, SkillDomain> skillDomainMap;
	private Map<Integer, BuffDomain> buffMap;

	private SkillService() {
		skillDomainMap = SkillXmlResolution.INSATNCE.skillResolution();
		buffMap = SkillXmlResolution.INSATNCE.buffResolution();
	}

	public ReplyDomain dealSkillEffect(RoleSkill roleSkill, LivingThing attackedObject, LivingThing beAttackedObject,
			long now) {
		SkillDomain domain = getSkillDomain(roleSkill.getSkillId());

		if (attackedObject.isContainBuff(5)) {
			return ReplyDomain.IN_HALO;
		}

		if (domain.isMagicSkill() && attackedObject.isContainBuff(6)) {
			return ReplyDomain.IN_SILENT;
		}

		// 是否在cd
		if (now - roleSkill.getLastUseTime() <= domain.getCd()) {
			return ReplyDomain.IN_CD;
		}

		// 扣除mp
		if (domain.getNeedMp() > attackedObject.getAttribute(4).getValue()) {
			return ReplyDomain.MP_NOT_ENOUGH;
		}
		attackedObject.effectAttribute(4, -domain.getNeedMp(), "使用技能消耗mp");

		for (Entry<Integer, Integer> entry : domain.getEffectAttribute().entrySet()) {
			if (entry.getKey() == 3 && entry.getValue() < 0) {
				beAttackedObject.effectAttribute(entry.getKey(), entry.getValue()
						- attackedObject.getAttribute(1).getValue() + beAttackedObject.getAttribute(2).getValue(),
						domain.getName() + "技能作用");
			} else {
				beAttackedObject.effectAttribute(entry.getKey(), entry.getValue(), domain.getName() + "技能作用");
			}
		}

		for (Entry<Integer, Integer> entry : domain.getSelfEffectAttribute().entrySet()) {
			attackedObject.effectAttribute(entry.getKey(), entry.getValue(), domain.getName() + "技能作用");
		}

		List<Integer> buffList = getSkillBuff(domain);
		for (Integer buffDomainId : buffList) {
			RoleBuff buff = dealBuffAttribute(buffDomainId);
			beAttackedObject.addBuff(buff);
		}

		roleSkill.setLastUseTime(now);

		return ReplyDomain.SUCCESS;
	}

	public void addBuff(Role role, int buffId) {

		RoleBuff buff = dealBuffAttribute(buffId);
		role.addBuff(buff);
	}

	public RoleBuff dealBuffAttribute(int buffId) {
		BuffDomain buffDomain = getBuffDomain(buffId);
		Map<Integer, Integer> map = buffDomain.getAttributeEffect();
		long now = System.currentTimeMillis();
		RoleBuff buff = new RoleBuff(buffDomain.getName(), buffDomain.getId(), now, map, buffDomain.isBuff(),
				buffDomain.isCanResore(), buffDomain.getLastTime(), buffDomain.getHeartbeatTime());

		return buff;
	}

	private List<Integer> getSkillBuff(SkillDomain domain) {
		Random random = new Random();
		int probability = random.nextInt(100);

		List<Integer> buffs = new ArrayList<>();

		for (Entry<Integer, Integer> entry : domain.getBuffprobabilityMap().entrySet()) {
			if (entry.getValue() >= probability) {
				buffs.add(entry.getKey());
			}
		}

		return buffs;
	}

	public SkillDomain getSkillDomain(int skillId) {
		SkillDomain domain = skillDomainMap.get(skillId);
		return domain;
	}

	public BuffDomain getBuffDomain(int buffId) {
		return buffMap.get(buffId);
	}
}
