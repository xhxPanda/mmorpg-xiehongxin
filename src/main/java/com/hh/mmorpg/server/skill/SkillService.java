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

		if (now - roleSkill.getLastUseTime() <= domain.getCd()) {
			return ReplyDomain.IN_CD;
		}

		for (Entry<Integer, Integer> entry : domain.getEffectAttribute().entrySet()) {
			beAttackedObject.effectAttribute(entry.getKey(), entry.getValue());
		}

		for (Entry<Integer, Integer> entry : domain.getSelfEffectAttribute().entrySet()) {
			attackedObject.effectAttribute(entry.getKey(), entry.getValue());
		}

		List<BuffDomain> buffList = getSkillBuff(domain);
		for (BuffDomain buffDomain : buffList) {
			RoleBuff buff = dealBuffAttribute(buffDomain);
			beAttackedObject.addBuff(buff);
		}

		roleSkill.setLastUseTime(now);

		return ReplyDomain.SUCCESS;
	}

	public void addBuff(Role role, int buffId) {
		BuffDomain buffDomain = getBuffDomain(buffId);

		RoleBuff buff = dealBuffAttribute(buffDomain);
		role.addBuff(buff);
	}

	private RoleBuff dealBuffAttribute(BuffDomain buffDomain) {
		Map<Integer, Integer> map = buffDomain.getAttributeEffect();
		long now = System.currentTimeMillis();
		RoleBuff buff = new RoleBuff(buffDomain.getId(), now, map, buffDomain.isBuff(), buffDomain.isCanResore(),
				buffDomain.getLastTime(), buffDomain.getHeartbeatTime());

		return buff;
	}

	private List<BuffDomain> getSkillBuff(SkillDomain domain) {
		Random random = new Random();
		int probability = random.nextInt(100);

		List<BuffDomain> buffs = new ArrayList<>();

		for (Entry<Integer, Integer> entry : domain.getBuffprobabilityMap().entrySet()) {
			if (entry.getValue() >= probability) {
				buffs.add(buffMap.get(entry.getKey()));
			}
		}

		return buffs;
	}

	public SkillDomain getSkillDomain(int skillId) {
		SkillDomain domain = skillDomainMap.get(skillId);
		return domain;
	}

	private BuffDomain getBuffDomain(int buffId) {
		return buffMap.get(buffId);
	}
}
