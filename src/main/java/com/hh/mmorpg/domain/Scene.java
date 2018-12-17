package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.event.data.PassCopyData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.server.scene.SceneUserCache;
import com.hh.mmorpg.server.skill.SkillService;
import com.hh.mmorpg.service.user.UserService;

/**
 * 
 * @author xhx 场景实体
 * 
 */
public class Scene {

	private int id;
	private int sceneTypeId;
	private String name;
	private List<Integer> neighborSceneIds;
	private boolean isCanBattle;
	private boolean isCopy;
	private long buildTime; // 生成场景的时间
	private int needLevel; // 场景等级限制

	private Map<Integer, Map<Integer, Monster>> monsterSetMap;// monster初始配置的怪物生成序列

	private Map<Integer, MonsterBeKillBonus> monsterBeKillBonusmap; // 每个角色的掉落的monster道具

	private ConcurrentHashMap<Integer, SceneUserCache> userMap = new ConcurrentHashMap<>();
	// 怪物列表
	private ConcurrentHashMap<Integer, Monster> monsterMap;
	// NPC列表
	private Map<Integer, NpcRole> npcRoleMap;
	// 召唤兽列表
	private Map<Integer, Map<Integer, SummonMonster>> summonMonstermap;

	private ScheduledExecutorService executorService;

	private AtomicInteger monsterRound;

	public Scene(SceneDomain domain, int id) {
		this.id = id;
		this.name = domain.getName();
		this.isCanBattle = domain.isCanBattle();
		this.neighborSceneIds = domain.getNeighborSceneIds();
		this.isCopy = domain.isCopy();
		this.monsterSetMap = new HashMap<>(domain.getMonsterSetMap());
		this.sceneTypeId = domain.getId();
		this.needLevel = domain.getId();

		this.buildTime = System.currentTimeMillis();

		// 当前怪物的轮数
		this.monsterRound = new AtomicInteger(0);

		// 生成第一批怪物
		this.monsterMap = new ConcurrentHashMap<>();
		if (monsterSetMap.size() != 0) {
			for (Monster monster : monsterSetMap.get(monsterRound.get()).values()) {
				monster.setSceneId(id);
				this.monsterMap.put(monster.getUniqueId(), monster);
			}
		}

		this.executorService = Executors.newSingleThreadScheduledExecutor();
		start();

		this.monsterBeKillBonusmap = new HashMap<>();
		this.npcRoleMap = domain.getNpcRoleMap();

		this.summonMonstermap = new HashMap<>();

		// 监听怪物死亡事件
		EventHandler.INSTANCE.addHandler(EventType.MONSTER_DEAD, monsterDeadEvent);
	}

	public boolean isCanEnter(int id) {
		return neighborSceneIds.contains(id);
	}

	public ReplyDomain userEnterScene(SceneUserCache sceneUserCache) {
		if (userMap.containsKey(sceneUserCache.getUserId())) {
			return ReplyDomain.FAILE;
		}
		userMap.put(sceneUserCache.getUserId(), sceneUserCache);
		ReplyDomain domain = new ReplyDomain();
		domain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_ENTER);
		domain.setStringDomain("角色名称", sceneUserCache.getRole().getName());
		domain.setIntDomain("角色id", sceneUserCache.getRole().getId());
		notifyOtherUser(sceneUserCache.getUserId(), domain);
		return ReplyDomain.SUCCESS;
	}

	public SceneUserCache userLeaveScene(int userId) {
		SceneUserCache sceneUserCache = userMap.remove(userId);
		ReplyDomain domain = new ReplyDomain();
		domain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_LEAVE);
		domain.setStringDomain("角色名称", sceneUserCache.getRole().getName());
		domain.setIntDomain("角色id", sceneUserCache.getRole().getId());
		notifyOtherUser(userId, domain);
		return sceneUserCache;
	}

	public void notifyOtherUser(int useId, ReplyDomain domain) {
		for (Entry<Integer, SceneUserCache> entry : userMap.entrySet()) {
			if (entry.getValue().getUserId() != useId) {

				SceneExtension.notifyUser(UserService.INSTANCE.getUser(entry.getValue().getUserId()), domain);
			}
		}
	}

	public void notifyAllUser(ReplyDomain domain) {
		for (Entry<Integer, SceneUserCache> entry : userMap.entrySet()) {
			SceneExtension.notifyUser(UserService.INSTANCE.getUser(entry.getValue().getUserId()), domain);
		}
	}

	public Role getUserRole(int userId) {
		return userMap.get(userId).getRole();
	}

	public Monster getMonster(int monsterId) {
		return monsterMap.get(monsterId);
	}

	public Map<Integer, NpcRole> getNpcRoleMap() {
		return npcRoleMap;
	}

	public void setNpcRoleMap(Map<Integer, NpcRole> npcRoleMap) {
		this.npcRoleMap = npcRoleMap;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isCopy() {
		return isCopy;
	}

	public long getBuildTime() {
		return buildTime;
	}

	public List<Integer> getNeighborSceneIds() {
		return neighborSceneIds;
	}

	public ConcurrentHashMap<Integer, SceneUserCache> getUserMap() {
		return userMap;
	}

	public int getSceneTypeId() {
		return sceneTypeId;
	}

	public SceneUserCache getSceneUserCache(int userId) {
		return userMap.get(userId);
	}

	public boolean isCanBattle() {
		return isCanBattle;
	}

	public Map<Integer, Map<Integer, SummonMonster>> getSummonMonstermap() {
		return summonMonstermap;
	}

	public Map<Integer, MonsterBeKillBonus> getMonsterBeKillBonusmap() {
		return monsterBeKillBonusmap;
	}

	public int getNeedLevel() {
		return needLevel;
	}

	public void putMonster(Monster monster) {
		monsterMap.put(monster.getUniqueId(), monster);
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_ENTRE);
		replyDomain.setIntDomain("怪物id", monster.getId());
		replyDomain.setStringDomain("怪物名称", monster.getName());
		notifyAllUser(replyDomain);
	}

	/**
	 * 新增掉落奖励
	 * 
	 * @param beKillBonus
	 */
	public void addRoleKillMonsterBonus(MonsterBeKillBonus beKillBonus) {

		monsterBeKillBonusmap.put(beKillBonus.getId(), beKillBonus);
	}

	/**
	 * 获取掉落物品
	 * 
	 * @param bonusId
	 * @return
	 */
	public MonsterBeKillBonus getRoleKillMonsterBonus(int bonusId) {
		MonsterBeKillBonus roleBonus = monsterBeKillBonusmap.get(bonusId);
		return roleBonus;
	}

	/**
	 * 获取场景中的掉落物品
	 * 
	 * @param roleId
	 * @return
	 */
	public List<MonsterBeKillBonus> getRoleKillMonsterBonusInfo(int roleId) {
		if (monsterBeKillBonusmap.get(roleId) != null) {
			return new ArrayList<MonsterBeKillBonus>(monsterBeKillBonusmap.values());
		} else {
			return null;
		}
	}

	public ConcurrentHashMap<Integer, Monster> getMonsterMap() {
		return monsterMap;
	}

	public void start() {
		// 场景心跳
		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				sceneHeartBeat();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * 心跳机制
	 */
	public void sceneHeartBeat() {
		// 怪物场景心跳判断怪物是否需要复活,或者需要受到buff的影响
		for (Monster monster : monsterMap.values()) {
			if (monster.isDead()) {
				long now = System.currentTimeMillis();
				if (monster.getFreshTime() == -1 || (now - monster.getBeKilledTime() < monster.getFreshTime())) {
					continue;
				} else {
					monster.resurrection();

					// 唤醒客户端
					ReplyDomain domain = new ReplyDomain();
					domain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_RESURRECTION);
					domain.setStringDomain("怪物", monster.toString());
					notifyAllUser(domain);
				}
			} else {
				monster.takeEffect();
				// 如果是副本，或者在非副本地区被攻击了的怪物（被动怪），怪物有自己的技能ai
				if (isCopy || monster.getAttackObject() != null) {
					monsterAIAttack(monster, getAllRole());
				}
			}
		}

		for (SceneUserCache cache : userMap.values()) {
			if (cache.getRole().isDead()) {
				continue;
			}
			cache.getRole().takeEffect();
		}

		List<SummonMonster> needRemoveSummonMonster = new ArrayList<>();
		// 召唤兽ai
		for (Map<Integer, SummonMonster> map : summonMonstermap.values()) {
			for (SummonMonster summonMonster : map.values()) {
				long now = System.currentTimeMillis();
				if (summonMonster.getTerminalTime() >= now) {
					needRemoveSummonMonster.add(summonMonster);
					continue;
				}

				monsterAIAttack(summonMonster, summonMonster.getTargetAttackObject());
			}

			for (SummonMonster summonMonster : needRemoveSummonMonster) {
				summonMonstermap.remove(summonMonster.getUniqueId());
			}
		}
	}

	/**
	 * 是否所有用户都死了
	 * 
	 * @return
	 */
	public boolean isAllDead() {
		for (SceneUserCache cache : userMap.values()) {
			if (!cache.getRole().isDead()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否所有怪物都死了
	 * 
	 * @return
	 */
	public boolean isAllMonsterDead() {
		for (Monster monster : monsterMap.values()) {
			if (!monster.isDead()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 场景是否已经没人了
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return userMap.size() == 0;
	}

	/**
	 * 获取场景中的npc
	 * 
	 * @param npcId
	 * @return
	 */
	public NpcRole getNpcRole(int npcId) {
		return npcRoleMap.get(npcId);
	}

	/**
	 * 场景怪物自动攻击
	 * 
	 * @param monster
	 * @param attackDistance
	 */
	private void monsterAIAttack(LivingThing monster, List<? extends LivingThing> attackDistance) {
		List<RoleSkill> monsterSkillList = new ArrayList<>(monster.getSkillMap().values());

		Random skillRandom = new Random();
		int randomSkillIndex = skillRandom.nextInt(monsterSkillList.size());

		// 在副本中有可能会没有指定目标
		if (isCopy && monster.getAttackObject() == null) {
			Random attackRandom = new Random();
			int randomIndex = attackRandom.nextInt(attackDistance.size());
			monster.setAttackObject(attackDistance.get(randomIndex));
		}

		SkillService.INSTANCE.dealSkillEffect(monsterSkillList.get(randomSkillIndex), monster, attackDistance,
				System.currentTimeMillis());
	}

	/**
	 * 获取所有的场景角色
	 * 
	 * @return
	 */
	public List<Role> getAllRole() {
		List<Role> roles = new ArrayList<>();

		for (SceneUserCache cache : userMap.values()) {
			roles.add(cache.getRole());
		}

		return roles;
	}

	/**
	 * 副本刷新怪物，如果没有代表副本已完成
	 * 
	 * @return
	 */
	private boolean refreshMonster() {
		if (isAllMonsterDead()) {
//			int monsterRoundId = monsterRound.get()
//			for (Monster monster : monsterSetMap.get(monsterRoundId).values()) {
//				monsterMap.remove(monster.getUniqueId());
//			}
			int roundId = monsterRound.incrementAndGet();
			if (monsterSetMap.size() <= roundId) {
				return false;
			}
			for (Monster monster : monsterSetMap.get(roundId).values()) {
				monster.setSceneId(id);
				putMonster(monster);
			}

		}
		return true;
	}

	/**
	 * 副本场景是否已经完结了
	 * 
	 * @return
	 */
	public boolean isCopyFinish() {
		return isAllDead() && (monsterSetMap.size() <= monsterRound.get());
	}

	/**
	 * 新增召唤物
	 * 
	 * @param roleId
	 * @param summonMonster
	 */
	public void addSummon(int roleId, SummonMonster summonMonster) {
		Map<Integer, SummonMonster> map = summonMonstermap.get(roleId);
		if (map == null) {
			map = new HashMap<>();
			summonMonstermap.put(roleId, map);
		}

		map.put(summonMonster.getUniqueId(), summonMonster);
	}

	/**
	 * 是不是竞技场
	 * 
	 * @return
	 */
	public boolean isPKScene() {
		return isCanBattle;
	}

	/*
	 * 停止当前副本的场景心跳
	 */
	public void shutdown() {
		monsterMap.clear();
		userMap.clear();
		executorService.shutdown();
	}

	// 副本刷新怪物
	public EventBuilder<MonsterDeadData> monsterDeadEvent = new EventBuilder<MonsterDeadData>() {

		@Override
		public void handler(MonsterDeadData data) {
			if (isCopy) {
				if (isAllMonsterDead()) {

					if (!refreshMonster()) {
						// 完成所有轮数的怪物，副本结束
						ReplyDomain replyDomain = new ReplyDomain();
						replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_COPY_FINISH);

						// 完成副本后全场的用户都抛出完成副本的事件
						for (SceneUserCache cache : userMap.values()) {
							PassCopyData passCopyData = new PassCopyData(cache.getRole(), sceneTypeId);
							EventHandler.INSTANCE.invodeMethod(EventType.PASS_COPY, passCopyData);
						}

						// 完成副本60分钟后解散副本
						SceneService.INSTANCE.finishScene(id);
						notifyAllUser(replyDomain);
					}
				}
			}
		}

	};

	@Override
	public String toString() {
		return "Scene [id=" + id + ", name=" + name + "]";
	}

}
