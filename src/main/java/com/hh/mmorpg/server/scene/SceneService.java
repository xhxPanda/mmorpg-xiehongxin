package com.hh.mmorpg.server.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.LivingThing;
import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.MonsterBeKillBonus;
import com.hh.mmorpg.domain.MonsterDomain;
import com.hh.mmorpg.domain.NpcRole;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.SceneDomain;
import com.hh.mmorpg.domain.SummonMonster;
import com.hh.mmorpg.domain.TeamMate;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.event.data.NpcTalkData;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.equiment.EquimentType;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.skill.SkillService;
import com.hh.mmorpg.server.team.TeamService;
import com.hh.mmorpg.service.user.UserService;

public class SceneService {

	public static final SceneService INSTANCE = new SceneService();

	private Map<Integer, SceneDomain> sceneDomainMap;
	private Map<Integer, MonsterDomain> monsterDomainmap;

	private Map<Integer, Scene> sceneMap;

	private AtomicInteger copyIncrease;

	private ScheduledExecutorService executorService;

	private SceneService() {
		sceneDomainMap = SceneXMLResolution.INSTANCE.resolution();

		monsterDomainmap = SceneXMLResolution.INSTANCE.resolutionMonster();
		this.copyIncrease = new AtomicInteger(0);

		// 生成固定的场景
		sceneMap = new HashMap<>();
		for (SceneDomain domain : sceneDomainMap.values()) {
			if (!domain.isCopy()) {
				int id = copyIncrease.incrementAndGet();
				sceneMap.put(id, new Scene(domain, id));
			}
		}

		executorService = Executors.newScheduledThreadPool(10);

		EventHandlerManager.INSATNCE.register(this);
	}

	public ReplyDomain userJoinScene(User user, int sceneTypeId, int sceneId) {
		int userId = user.getUserId();

		SceneUserCache sceneUserCache = null;

		// 初始进入场景
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		if (role == null) {
			return ReplyDomain.FAILE;
		}

		// 正在交易不能离开场景
		if (role.getTransactionPerson() != 0) {
			return ReplyDomain.IN_TRANSACTION;
		}

		// 获取之前用户的场景
		int oldSceneId = role.getSceneId();

		if (oldSceneId != 0) {
			if (oldSceneId == sceneTypeId) {
				return ReplyDomain.SUCCESS;
			}
			Scene scene = sceneMap.get(oldSceneId);
			if (!scene.isCanEnter(sceneTypeId)) {
				return new ReplyDomain(ResultCode.CAN_NOT_ENTER);
			}
			sceneUserCache = scene.getSceneUserCache(userId);
			sceneUserCache.setLastSceneId(oldSceneId);
		} else {
			sceneUserCache = new SceneUserCache(userId, role);
		}

		SceneDomain scenedomain = sceneDomainMap.get(sceneTypeId);
		if (scenedomain == null) {
			return ReplyDomain.FAILE;
		}

		// 进入新场景
		Scene newScene = sceneMap.get(sceneId);
		if (newScene.userEnterScene(sceneUserCache).isSuccess()) {
			role.setSceneId(sceneId);
		}

		// 设置离开前的那个场景
		role.setLastJoinScene(sceneId);

		if (oldSceneId != 0) {
			Scene scene = sceneMap.get(oldSceneId);
			scene.userLeaveScene(userId);
		}

		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("场景名称", newScene.getName());
		replyDomain.setStringDomain("场景名称", newScene.getName());
		replyDomain.setListDomain("角色列表", newScene.getUserMap().values());
		replyDomain.setListDomain("npc角色列表", newScene.getNpcRoleMap().values());
		replyDomain.setListDomain("怪物列表", newScene.getMonsterMap().values());
		return replyDomain;
	}

	/**
	 * 断线重连，强制进入副本
	 * 
	 * @param role
	 */
	public void userJoinLastCopy(Role role) {
		int lastJoinSceneId = role.getLastJoinScene();

		Scene scene = sceneMap.get(lastJoinSceneId);

		User user = UserService.INSTANCE.getUser(role.getUserId());

		// 不是副本就不需要强制进入
		if (scene == null || !scene.isCopy() || scene.isCopyFinish()) {
			role.setLastJoinScene(0);
			return;
		}

		SceneUserCache sceneUserCache = new SceneUserCache(role.getUserId(), role);
		scene.userEnterScene(sceneUserCache);

		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("场景名称", scene.getName());
		replyDomain.setStringDomain("场景名称", scene.getName());
		replyDomain.setListDomain("角色列表", scene.getUserMap().values());
		replyDomain.setListDomain("npc角色列表", scene.getNpcRoleMap().values());
		replyDomain.setListDomain("怪物列表", scene.getMonsterMap().values());
		SceneExtension.notifyUser(user, replyDomain);
	}

	/**
	 * 进入副本
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain joinCopyScene(User user, int sceneTypeId, int sceneId) {
		// TODO Auto-generated method stub

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		// 获取用户之前的scene
		int oldSceneId = role.getSceneId();
		if (oldSceneId == 0) {
			return ReplyDomain.FAILE;
		}
		Scene oldScene = sceneMap.get(oldSceneId);

		// 获取副本属性
		SceneDomain scenedomain = sceneDomainMap.get(sceneTypeId);

		Scene newScene = null;
		// 单人进入

		List<SceneUserCache> caches = new ArrayList<>();
		if (role.getTeamId() == 0) {
			if (scenedomain.getEntreNumLimit() != 1) {
				return ReplyDomain.COPY_NUM_NOT_MATCH;
			}

			SceneUserCache sceneUserCache = oldScene.getSceneUserCache(user.getUserId());
			caches.add(sceneUserCache);
		} else {
			// 组队进入
			Map<Integer, TeamMate> team = TeamService.INSTANCE.getTeam(role.getTeamId());
			TeamMate myTeamMate = team.get(role.getId());

			// 如果不是队长，不能操作
			if (!myTeamMate.isTeamLeader()) {
				return ReplyDomain.IS_NOT_TEAM_LEADER;
			}

			for (TeamMate teamMate : team.values()) {
				// 必须所有人员在线
				if (!RoleService.INSTANCE.isOnline(teamMate.getRoleId())) {
					return ReplyDomain.TEAM_NOT_ALL_ONLINE;
				}

				// 必须所有人员在同一场景
				SceneUserCache sceneUserCache = oldScene
						.getSceneUserCache(RoleService.INSTANCE.getUserId(teamMate.getRoleId()));
				if (sceneUserCache == null) {
					return ReplyDomain.TEAM_NOT_ALL_IN_SAME_SCENE;
				}

				if (sceneUserCache.getRole().getTransactionPerson() != 0) {
					return ReplyDomain.IN_TRANSACTION;
				}

				caches.add(sceneUserCache);
			}
		}

		newScene = entreCopy(oldScene, caches, sceneTypeId);
		sceneMap.put(newScene.getId(), newScene);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 进入副本，统一逻辑
	 * 
	 * @param sceneUserCaches
	 * @param sceneTypeId
	 * @return
	 */
	private Scene entreCopy(Scene oldScene, List<SceneUserCache> sceneUserCaches, int sceneTypeId) {
		SceneDomain sceneDomain = sceneDomainMap.get(sceneTypeId);

		Scene scene = null;

		int sceneId = 0;
		if (sceneDomain != null) {
			sceneId = copyIncrease.incrementAndGet();
			scene = new Scene(sceneDomain, sceneId);
		}

		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		replyDomain.setStringDomain("场景名称", scene.getName());
		replyDomain.setStringDomain("场景名称", scene.getName());
		replyDomain.setListDomain("角色列表", scene.getUserMap().values());
		replyDomain.setListDomain("npc角色列表", scene.getNpcRoleMap().values());
		replyDomain.setListDomain("怪物列表", scene.getMonsterMap().values());

		for (SceneUserCache cache : sceneUserCaches) {
			scene.userEnterScene(cache);
			sceneMap.put(sceneId, scene);
			cache.getRole().setSceneId(sceneId);

			RoleService.INSTANCE.getUserUsingRole(cache.getUserId()).setLastJoinScene(sceneId);
			// 离开旧场景
			oldScene.userLeaveScene(cache.getUserId());
			User user = UserService.INSTANCE.getUser(cache.getUserId());

			// 通知前端
			SceneExtension.notifyUser(user, replyDomain);
		}

		return scene;
	}

	public void finishScene(int sceneId) {
		// 生成副本后，60分钟就把人踢出来（不管有无完成）
		executorService.schedule(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				userForceLeaveCopy(sceneId);
			}
		}, 60, TimeUnit.MINUTES);
	}

	public ReplyDomain getSeceneUser(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		ReplyDomain domain = new ReplyDomain();

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);

		domain.setStringDomain("场景名称", scene.getName());
		domain.setListDomain("u", scene.getUserMap().values());
		domain.setListDomain("npc角色列表", scene.getNpcRoleMap().values());
		domain.setListDomain("怪物列表", scene.getMonsterMap().values());

		return domain;
	}

	public ReplyDomain attackMonster(User user, int skillId, int monsterId) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		Monster monster = scene.getMonster(monsterId);
		if (monster == null) {
			return ReplyDomain.FAILE;
		}

		if (monster.isDead()) {
			return ReplyDomain.HAS_DEAD;
		}

		RoleSkill roleSkill = role.getRoleSkill(skillId);
		if (roleSkill == null) {
			return ReplyDomain.FAILE;
		}

		UserEquipment userEquipment = role.getEquipmentMap().get(EquimentType.ARMS);
		if (userEquipment == null || !userEquipment.dropDurability().isSuccess()) {
			return ReplyDomain.EQUIMENT_DURABILITY_HARM;
		} 
		
		long now = System.currentTimeMillis();

		setAttackObject(scene, role, monster);

		ReplyDomain replyDomain = SkillService.INSTANCE.dealSkillEffect(roleSkill, role,
				new ArrayList<>(scene.getMonsterMap().values()), now);
		if (!replyDomain.isSuccess()) {
			return replyDomain;
		}

		ReplyDomain notifyReplyDomain = new ReplyDomain();
		notifyReplyDomain.setStringDomain("m", monster.toString());
		notifyReplyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_BE_ATTACK);
		scene.notifyAllUser(notifyReplyDomain);
		
		ReplyDomain domain = new ReplyDomain("攻击怪物" + ResultCode.SUCCESS);
		return domain;
	}

	// 修改pk规则，只能在pk场
	public ReplyDomain attackOtherRole(User user, int skillId, int otherUserId) {
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		if (!scene.isCanBattle() || role.getAttackObject() == null) {
			return ReplyDomain.CAN_NOT_BATTLE;
		}

		RoleSkill roleSkill = role.getRoleSkill(skillId);
		if (roleSkill == null) {
			return ReplyDomain.FAILE;
		}

		Role otherRole = scene.getUserRole(otherUserId);
		if (otherRole == null) {
			return ReplyDomain.FAILE;
		}

		// 正在pk中的人只能打pk对象
		if (role.getPkRoleId() != 0 && role.getPkRoleId() != otherRole.getId()) {
			return ReplyDomain.FAILE;
		}

		setAttackObject(scene, role, otherRole);

		long now = System.currentTimeMillis();

		List<LivingThing> livingThings = new ArrayList<>();
		if (role.getPkRoleId() != 0) {
			// 在pk当中只能打pk对象，就算是发群体技能
			livingThings.add(otherRole);
			SkillService.INSTANCE.dealSkillEffect(roleSkill, role, livingThings, now);
		} else {

			// 群体攻击避开队友
			if (role.getTeamId() != 0) {
				Map<Integer, TeamMate> map = TeamService.INSTANCE.getTeam(role.getTeamId());

				for (Role r : scene.getAllRole()) {
					if (map.containsKey(r.getId())) {
						livingThings.add(r);
					}
				}
			} else {
				livingThings.addAll(scene.getAllRole());
			}

			SkillService.INSTANCE.dealSkillEffect(roleSkill, role, livingThings, now);
		}

		ReplyDomain domain = new ReplyDomain(ResultCode.SUCCESS);
		return domain;
	}

	/**
	 * 设置打击对象，如果有召唤兽的话，改变其行为模式
	 * 
	 * @param scene
	 * @param role
	 * @param target
	 */
	private void setAttackObject(Scene scene, LivingThing attackObject, LivingThing target) {
		attackObject.setAttackObject(target);

		Map<Integer, SummonMonster> summonMonsterMap = scene.getSummonMonstermap().get(attackObject.getId());
		if (summonMonsterMap == null) {
			return;
		}

		for (SummonMonster summonMonster : summonMonsterMap.values()) {
			summonMonster.setAttackObject(target);
		}
	}

	// 获取用户所在的场景
	public Scene getUserScene(int userId) {

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		if (role == null)
			return null;

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return null;
		}
		return sceneMap.get(sceneId);
	}

	public Map<Integer, Scene> getSceneMap() {
		return sceneMap;
	}

	public ReplyDomain getRoleKillMonsterBonusInfo(User user) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		List<MonsterBeKillBonus> monsterBeKillBonus = scene.getRoleKillMonsterBonusInfo(role.getId());

		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		if (monsterBeKillBonus != null) {
			replyDomain.setListDomain("bonusList", monsterBeKillBonus);
		}

		return replyDomain;
	}

	public ReplyDomain getRoleKillMonsterBonus(User user, int bonusId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		MonsterBeKillBonus bonus = scene.getRoleKillMonsterBonus(bonusId);
		if (bonus == null) {
			return ReplyDomain.BONS_NOT_EXIT;
		}

		// 正在交易，不能捡取物品
		if (role.getTransactionPerson() != 0) {
			return ReplyDomain.IN_TRANSACTION;
		}

		if (role.getTeamId() != 0) {
			// 组队中的人就随机分配
			Map<Integer, TeamMate> team = TeamService.INSTANCE.getTeam(role.getTeamId());

			int i = 0;
			for (TeamMate teamMate : team.values()) {
				if (bonus.getRoleId() != teamMate.getRoleId()) {
					i++;
				} else {
					continue;
				}
			}

			// 不是队伍中的人打出来的
			if (i == team.size()) {
				return ReplyDomain.FAILE;
			}

			List<Integer> roleIdList = new ArrayList<>();
			for (TeamMate teamMate : team.values()) {
				// 下线没有分配的资格
				if (teamMate.isOnline()) {
					roleIdList.add(teamMate.getRoleId());
				}
			}

			Random random = new Random();
			int getBonusRoleId = roleIdList.get(random.nextInt(roleIdList.size()));

			User getBonusUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(getBonusRoleId));
			Role getBonusRole = RoleService.INSTANCE.getUserUsingRole(getBonusUser.getUserId());
			MaterialService.INSTANCE.gainMasteral(getBonusUser, getBonusRole, bonus.getBonus());
			MaterialService.INSTANCE.gainMasteral(user, role, bonus.getBonus());
		} else {

			if (bonus.getRoleId() != role.getId()) {
				return ReplyDomain.FAILE;
			}
			ReplyDomain gainMaterialResult = MaterialService.INSTANCE.gainMasteral(user, role, bonus.getBonus());
			if (!gainMaterialResult.isSuccess()) {
				return ReplyDomain.FAILE;
			}
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain putMonsterIntoScene(int userId, int MonsterId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		MonsterDomain monsterDomain = monsterDomainmap.get(MonsterId);

		int uniqueId = IncrementManager.INSTANCE.increase("monster");
		Monster monster = new Monster(uniqueId, scene.getId(), monsterDomain, false);

		scene.putMonster(monster);
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain taklToNpc(User user, int npcId) {
		int userId = user.getUserId();
		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);

		NpcRole npcRole = scene.getNpcRole(npcId);
		if (npcRole == null) {
			return ReplyDomain.NPC_NOT_EXIT;
		}

		NpcTalkData data = new NpcTalkData(role, npcId);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.TALK_TO_NPC, new EventDealData<NpcTalkData>(data));

		return ReplyDomain.SUCCESS;
	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		Role role = userLostData.getRole();

		int sceneId = role.getSceneId();
		if (sceneId == 0) {
			return;
		}
		Scene scene = sceneMap.get(sceneId);

		role.setSceneId(0);
		scene.userLeaveScene(userLostData.getUser().getUserId());
		judgeScene(scene);
		System.out.println("用户下线了");
	}

	// 用户切换角色后把场景中的角色移除
	@Event(eventType = EventType.ROLE_CHANGE)
	public void handleRoleChange(EventDealData<RoleChangeData> data) {
		int userId = data.getData().getUserId();

		Role oldRole = data.getData().getOldRole();
		if(oldRole == null)
			return;

		Scene scene = sceneMap.get(oldRole.getSceneId());
		
		scene.userLeaveScene(userId);
		judgeScene(scene);
	}

	// 监听怪兽死亡的事件，掉落物品
	@Event(eventType = EventType.MONSTER_DEAD)
	public void handleMonsterDead(EventDealData<MonsterDeadData> data) {
		MonsterDeadData monsterDeadData = data.getData();

		Scene scene = sceneMap.get(monsterDeadData.getSceneId());

		Monster monster = scene.getMonster(monsterDeadData.getMonsterId());
		String bonus = monsterDeadBonus(monster.getKillFallItemMap());

		if (!bonus.isEmpty()) {

			int id = IncrementManager.INSTANCE.increase("monsterBeKillBonus");
			MonsterBeKillBonus monsterBeKillBonus = new MonsterBeKillBonus(id, monsterDeadData.getKillRoleId(),
					monsterDeadData.getMonsterId(), System.currentTimeMillis(), bonus);
			scene.addRoleKillMonsterBonus(monsterBeKillBonus);

			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("bonus", monsterBeKillBonus.toString());
			replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_ROLE_MONSTER_BONUS_FALL);

			// 通知前端奖励加入场景
			scene.notifyAllUser(replyDomain);
		}

	}

	// 判断该场景是否副本，是的话判断是否已经没人了，没人的话就移除
	private void judgeScene(Scene scene) {
		if (scene.isCopy() && scene.isEmpty()) {
			removeScene(scene);
		}
	}

	private void removeScene(Scene scene) {
		sceneMap.remove(scene.getId());
		scene.shutdown();
	}

	/**
	 * 强制用户退出
	 * 
	 * @param sceneId
	 */
	private void userForceLeaveCopy(int sceneId) {
		Scene scene = sceneMap.get(sceneId);

		// 提醒用户该副本超出了时间限制
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_COPY_BEYOND_TIME);
		scene.notifyAllUser(replyDomain);
		removeScene(scene);
		// 执行移除操作，用户回到上一次所在的地图
		for (SceneUserCache sceneUserCache : scene.getUserMap().values()) {

			User user = UserService.INSTANCE.getUser(sceneUserCache.getUserId());
			userJoinScene(user, scene.getSceneTypeId(), sceneUserCache.getLastSceneId());
		}
	}

	/**
	 * 组合死亡后的bonus
	 * 
	 * @param killFallItemMap
	 * @return
	 */
	private String monsterDeadBonus(Map<String, Integer> killFallItemMap) {
		Random random = new Random();
		int randomNum = random.nextInt(100);

		StringBuilder builder = new StringBuilder();
		for (Entry<String, Integer> entry : killFallItemMap.entrySet()) {

			if (randomNum <= entry.getValue()) {
				if (builder.length() > 0) {
					builder.append("#");
				}
				builder.append(entry.getKey());
			}
		}

		return builder.toString();
	}

}