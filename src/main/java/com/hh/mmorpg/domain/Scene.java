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

import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;
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
	private Map<Integer, Map<Integer, Monster>> monsterSetMap;// monster初始配置的怪物生成序列

	private Map<Integer, Map<Integer, MonsterBeKillBonus>> monsterBeKillBonusmap; // 每个角色的掉落的monster道具

	private ConcurrentHashMap<Integer, SceneUserCache> userMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Monster> monsterMap;
	private Map<Integer, NpcRole> npcRoleMap;

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

		this.buildTime = System.currentTimeMillis();

		// 当前怪物的轮数
		this.monsterRound = new AtomicInteger(0);

		// 生成第一批怪物
		this.monsterMap = new ConcurrentHashMap<>();
		for (Monster monster : monsterSetMap.get(monsterRound.get()).values()) {
			monster.setSceneId(id);
			this.monsterMap.put(monster.getUniqueId(), monster);
		}

		this.executorService = Executors.newSingleThreadScheduledExecutor();
		start();

		this.monsterBeKillBonusmap = new HashMap<>();
		this.npcRoleMap = domain.getNpcRoleMap();

		// 监听怪物死亡事件
		EventHandlerManager.INSATNCE.register(this);
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

	public void putMonster(Monster monster) {
		monsterMap.put(monster.getUniqueId(), monster);
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_ENTRE);
		replyDomain.setIntDomain("怪物id", monster.getId());
		replyDomain.setStringDomain("怪物名称", monster.getName());
		notifyAllUser(replyDomain);
	}

	public void addRoleKillMonsterBonus(int roleId, MonsterBeKillBonus beKillBonus) {

		Map<Integer, MonsterBeKillBonus> killBonusMap = monsterBeKillBonusmap.get(roleId);
		if (killBonusMap == null) {
			killBonusMap = new HashMap<>();
			monsterBeKillBonusmap.put(roleId, killBonusMap);
		}
		killBonusMap.put(beKillBonus.getId(), beKillBonus);
	}

	public MonsterBeKillBonus getRoleKillMonsterBonus(int roleId, int bonusId) {
		Map<Integer, MonsterBeKillBonus> roleBonusMap = monsterBeKillBonusmap.get(roleId);
		if (roleBonusMap == null || roleBonusMap.size() == 0) {
			return null;
		}
		return roleBonusMap.get(bonusId);
	}

	public List<MonsterBeKillBonus> getRoleKillMonsterBonusInfo(int roleId) {
		if (monsterBeKillBonusmap.get(roleId) != null) {
			return new ArrayList<MonsterBeKillBonus>(monsterBeKillBonusmap.get(roleId).values());
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
				// TODO Auto-generated method stub
				sceneHeartBeat();
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

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

				// 如果是副本，怪物有自己的技能ai
				if (isCopy) {
					monsterAIAttack(monster);
				}
			}
		}

		for (SceneUserCache cache : userMap.values()) {
			cache.getRole().takeEffect();
		}
	}

	public boolean isAllDead() {
		for (SceneUserCache cache : userMap.values()) {
			if (!cache.getRole().isDead()) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllMonsterDead() {
		for (Monster monster : monsterMap.values()) {
			if (!monster.isDead()) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return userMap.size() == 0;
	}

	private void monsterAIAttack(Monster monster) {
		List<RoleSkill> monsterSkillList = new ArrayList<>(monster.getSkillMap().values());

		Random random = new Random();
		int randomSkillIndex = random.nextInt(monsterSkillList.size());

		List<SceneUserCache> caches = new ArrayList<>(userMap.values());

		SkillService.INSTANCE.dealSkillEffect(monsterSkillList.get(randomSkillIndex), monster, caches.get(0).getRole(),
				System.currentTimeMillis());
	}

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

	public void shutdown() {
		monsterMap.clear();
		userMap.clear();
		executorService.shutdown();
	}

	// 副本刷新怪物
	@Event(eventType = EventType.MONSTER_DEAD)
	public void handleMonsterDead(EventDealData<MonsterDeadData> data) {
		if (isCopy) {
			if (isAllMonsterDead()) {

				if (!refreshMonster()) {
					// 完成所有轮数的怪物，副本结束
					ReplyDomain replyDomain = new ReplyDomain();
					replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_COPY_FINISH);
					notifyAllUser(replyDomain);
				}
			}
		}
	}
}
