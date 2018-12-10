package com.hh.mmorpg.server.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Random;

import com.hh.mmorpg.domain.AttributeEnum;
import com.hh.mmorpg.domain.BuffDomain;
import com.hh.mmorpg.domain.LivingThing;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleBuff;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.SkillDomain;
import com.hh.mmorpg.domain.SummonDomain;
import com.hh.mmorpg.domain.SummonMonster;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;

public class SkillService {

	public static final SkillService INSTANCE = new SkillService();

	private Map<Integer, SkillDomain> skillDomainMap;
	private Map<Integer, BuffDomain> buffMap;

	private Map<Integer, SummonDomain> summonDomainmap;

	private SkillService() {
		skillDomainMap = SkillXmlResolution.INSATNCE.skillResolution();
		buffMap = SkillXmlResolution.INSATNCE.buffResolution();

		summonDomainmap = SkillXmlResolution.INSATNCE.getSummonMonsterMap();
	}

	/**
	 * 学习技能
	 * 
	 * @param user
	 * @param skillId
	 * @return
	 */
	public ReplyDomain learnSkill(User user, int skillId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		SkillDomain skillDomain = skillDomainMap.get(skillId);

		if (skillDomain.getNeedLevel() > role.getLevel()) {
			return ReplyDomain.LEVEL_NOT_ENOUGH;
		}
		// 不符合职业
		if (skillDomain.getOccupationId() != -1 && skillDomain.getOccupationId() != role.getOccupationId()) {
			return ReplyDomain.OCCUPATION_NOT_MATCH;
		}

		// 技能已学, 就升级
		if (role.getSkillMap().containsKey(skillId)) {
			role.getSkillMap().get(skillId).addLevel();
			return ReplyDomain.SUCCESS;
		}

		RoleSkill roleSkill = new RoleSkill(role.getId(), skillId, 1);

		role.learnNewSkill(roleSkill);
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 处理技能生效
	 * 
	 * @param roleSkill
	 * @param attackedObject
	 * @param beAttackedObject
	 * @param now
	 * @return
	 */
	public ReplyDomain dealSkillEffect(RoleSkill roleSkill, LivingThing attackedObject,
			List<? extends LivingThing> livingThings, long now) {
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

		// 确定打击对象
		List<LivingThing> needAttackLivingThings = new ArrayList<>();

		// 群体攻击，无差别攻击
		if (domain.getSkillType() == 1) {
			needAttackLivingThings.addAll(livingThings);
		} else {
			// 单体攻击

			if (attackedObject.getAttackObject().isDead()) {
				return ReplyDomain.HAS_DEAD;
			}

			attackedObject.effectAttribute(0, 4, -domain.getNeedMp(), "使用技能消耗mp");

			if (attackedObject.getAttackObject() == null) {
				return ReplyDomain.FAILE;
			}

			int needAttackId = attackedObject.getAttackObject().getUniqueId();

			for (int i = 0; i < livingThings.size(); i++) {
				if (needAttackId == livingThings.get(i).getUniqueId() && !livingThings.get(i).isDead()) {
					needAttackLivingThings.add(livingThings.get(i));
					break;
				}
			}
		}

		// 扣除mp，如果失败就退出
		if (domain.getNeedMp() > attackedObject.getAttribute(4).getValue()) {
			return ReplyDomain.MP_NOT_ENOUGH;
		}

		if (needAttackLivingThings.size() != 0) {
			for (LivingThing beAttackedObject : needAttackLivingThings) {
				if(attackedObject instanceof SummonMonster) {
					beAttackedObject.setAttackObject(((SummonMonster) attackedObject).getBelonger());
				}else {
					beAttackedObject.setAttackObject(attackedObject);
				}
				
				for (Entry<Integer, Integer> entry : domain.getEffectAttribute().entrySet()) {
					// 具有伤害性的技能，需要计算防御值等
					if (entry.getKey() == AttributeEnum.HP.getId() && entry.getValue() < 0) {
						beAttackedObject.effectAttribute(roleSkill.getRoleId(), entry.getKey(),
								entry.getValue() - attackedObject.getAttribute(1).getValue()
										+ beAttackedObject.getAttribute(2).getValue(),
								attackedObject.getName() + "使用" + domain.getName() + "技能");
					} else {
						beAttackedObject.effectAttribute(roleSkill.getRoleId(), entry.getKey(), entry.getValue(),
								domain.getName() + "技能作用");
					}
				}
				List<Integer> buffList = getSkillBuff(domain);
				for (Integer buffDomainId : buffList) {
					RoleBuff buff = dealBuffAttribute(attackedObject.getId(), buffDomainId);
					beAttackedObject.addBuff(buff);
				}

			}
		}

		for (Entry<Integer, Integer> entry : domain.getSelfEffectAttribute().entrySet()) {
			attackedObject.effectAttribute(roleSkill.getRoleId(), entry.getKey(), entry.getValue(),
					domain.getName() + "技能作用");
		}

		// 召唤兽加入场景
		if (!domain.getSummon().isEmpty()) {
			Scene scene = SceneService.INSTANCE.getSceneMap().get(attackedObject.getSceneId());

			for (String s : domain.getSummon().split(",")) {

				String[] strs = s.split(":");

				int summonId = Integer.parseInt(strs[0]);
				long inistTime = Long.parseLong(strs[1]);

				SummonDomain summonDomain = summonDomainmap.get(summonId);
				scene.addSummon(attackedObject.getId(),
						new SummonMonster(attackedObject, 1, attackedObject.getSceneId(), summonDomain,
								inistTime, livingThings, attackedObject.getAttackObject()));
			}
		}

		roleSkill.setLastUseTime(now);
		return ReplyDomain.SUCCESS;
	}

	public void addBuff(Role role, int buffId) {

		RoleBuff buff = dealBuffAttribute(0, buffId);
		role.addBuff(buff);
	}

	public RoleBuff dealBuffAttribute(int objectId, int buffId) {
		BuffDomain buffDomain = getBuffDomain(buffId);
		Map<Integer, Integer> map = buffDomain.getAttributeEffect();
		long now = System.currentTimeMillis();
		RoleBuff buff = new RoleBuff(objectId, buffDomain.getName(), buffDomain.getId(), now, map, buffDomain.isBuff(),
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

	/**
	 * 获取用户可以学习的技能
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain getCanLearnSkill(User user) {
		// TODO Auto-generated method stub

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		List<SkillDomain> skillDomains = new ArrayList<>();

		for (SkillDomain skillDomain : skillDomainMap.values()) {
			// 职业不对
			if (skillDomain.getOccupationId() != -1 && skillDomain.getOccupationId() != role.getOccupationId()) {
				continue;
			}
			// 等级不对
			if (skillDomain.getNeedLevel() > role.getLevel()) {
				continue;
			}
			skillDomains.add(skillDomain);
		}
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("可学习的技能", skillDomains);
		return replyDomain;
	}
}
