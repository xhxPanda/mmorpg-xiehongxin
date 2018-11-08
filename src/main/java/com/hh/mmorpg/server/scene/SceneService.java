package com.hh.mmorpg.server.scene;

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

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.MonsterBeKillBonus;
import com.hh.mmorpg.domain.MonsterDomain;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.SceneDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.MonsterDeadData;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.equiment.EquimentType;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.skill.SkillService;
import com.hh.mmorpg.service.user.UserService;

public class SceneService {

	public static final SceneService INSTANCE = new SceneService();

	private Map<Integer, SceneDomain> sceneDomainMap;
	private Map<Integer, MonsterDomain> monsterDomainmap;

	private Map<Integer, Scene> sceneMap;
	private ConcurrentHashMap<Integer, Integer> sceneUserMap;

	private AtomicInteger copyIncrease;

	private ScheduledExecutorService executorService;

	private SceneService() {
		sceneUserMap = new ConcurrentHashMap<Integer, Integer>();
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

		// 生成用户场景缓存
		Integer oldSceneId = sceneUserMap.get(userId);
		if (oldSceneId != null) {
			if (oldSceneId == sceneTypeId) {
				return ReplyDomain.SUCCESS;
			}
			Scene scene = sceneMap.get(oldSceneId);

			if (!scene.isCanEnter(sceneTypeId)) {
				return new ReplyDomain(ResultCode.CAN_NOT_ENTER);
			}
			sceneUserCache = scene.userLeaveScene(userId);
			sceneUserCache.setLastSceneId(oldSceneId);
		} else {
			// 初始进入场景
			Role role = RoleService.INSTANCE.getUserUsingRole(userId);
			if (role == null) {
				return ReplyDomain.FAILE;
			}
			sceneUserCache = new SceneUserCache(userId, role);
		}

		SceneDomain scenedomain = sceneDomainMap.get(sceneTypeId);
		if (scenedomain == null) {
			return ReplyDomain.FAILE;
		}
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		if (scenedomain.isCopy()) {
			if (oldSceneId == null) {
				return ReplyDomain.FAILE;
			}
			sceneId = entreCopy(sceneUserCache, sceneTypeId);
		} else {
			// 进入新场景
			Scene newScene = sceneMap.get(sceneId);
			if (newScene.userEnterScene(sceneUserCache).isSuccess()) {
				sceneUserMap.put(userId, sceneId);
			}
			replyDomain.setStringDomain("场景名称", newScene.getName());

			replyDomain.setStringDomain("场景名称", newScene.getName());
			replyDomain.setListDomain("u", newScene.getUserMap().values());
			replyDomain.setListDomain("npc角色列表", newScene.getNpcRoleMap().values());
			replyDomain.setListDomain("怪物列表", newScene.getMonsterMap().values());
		}

		return replyDomain;
	}

	private int entreCopy(SceneUserCache sceneUserCache, int sceneTypeId) {
		SceneDomain sceneDomain = sceneDomainMap.get(sceneTypeId);

		Scene scene = null;

		int sceneId = 0;
		if (sceneDomain != null) {
			sceneId = copyIncrease.incrementAndGet();
			scene = new Scene(sceneDomain, sceneId);
		}

		if (scene.userEnterScene(sceneUserCache).isSuccess()) {
			sceneMap.put(sceneId, scene);
			sceneUserMap.put(sceneUserCache.getUserId(), sceneId);
		}

		finishScene(sceneId);
		return sceneId;
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

		ReplyDomain domain = new ReplyDomain();

		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
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
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
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

		Role role = scene.getUserRole(userId);
		RoleSkill roleSkill = role.getRoleSkill(skillId);
		if (roleSkill == null) {
			return ReplyDomain.FAILE;
		}

		UserEquipment userEquipment = role.getEquipmentMap().get(EquimentType.ARMS);
		if (userEquipment == null || !userEquipment.dropDurability().isSuccess()) {
			return ReplyDomain.EQUIMENT_DURABILITY_HARM;
		}

		long now = System.currentTimeMillis();

		if (monster.getAttackRoleId() == 0) {
			monster.setAttackRole(role.getId());
		}

		ReplyDomain replyDomain = SkillService.INSTANCE.dealSkillEffect(roleSkill, role, monster, now);
		if (!replyDomain.isSuccess()) {
			return replyDomain;
		}

		ReplyDomain notifyReplyDomain = new ReplyDomain();
		notifyReplyDomain.setStringDomain("m", monster.toString());
		notifyReplyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_BE_ATTACK);
		scene.notifyAllUser(notifyReplyDomain);
		ReplyDomain domain = new ReplyDomain(ResultCode.SUCCESS);
		return domain;
	}

	public ReplyDomain attackOtherRole(User user, int skillId, int otherUserId) {
		int userId = user.getUserId();
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		if (!scene.isCanBattle()) {
			return ReplyDomain.CAN_NOT_BATTLE;
		}

		Role role = scene.getUserRole(userId);
		RoleSkill roleSkill = role.getRoleSkill(skillId);
		if (roleSkill == null) {
			return ReplyDomain.FAILE;
		}

		Role otherRole = scene.getUserRole(otherUserId);
		if (otherRole == null) {
			return ReplyDomain.FAILE;
		}
		long now = System.currentTimeMillis();
		ReplyDomain replyDomain = SkillService.INSTANCE.dealSkillEffect(roleSkill, role, otherRole, now);
		if (!replyDomain.isSuccess()) {
			return ReplyDomain.FAILE;
		}
		ReplyDomain domain = new ReplyDomain(ResultCode.SUCCESS);
		return domain;
	}

	// 获取用户所在的场景
	public Scene getUserScene(int userId) {
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return null;
		}
		return sceneMap.get(sceneId);
	}

	public Map<Integer, Scene> getSceneMap() {
		return sceneMap;
	}

	public ReplyDomain getRoleKillMonsterBonusInfo(User user) {
		int userId = user.getUserId();
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);

		List<MonsterBeKillBonus> monsterBeKillBonus = scene.getRoleKillMonsterBonusInfo(role.getId());
		
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		if (monsterBeKillBonus != null) {
			replyDomain.setListDomain("bonusList", monsterBeKillBonus);
		}

		return replyDomain;
	}

	public ReplyDomain getRoleKillMonsterBonus(User user, int bonusId) {
		int userId = user.getUserId();
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		Role role = RoleService.INSTANCE.getUserUsingRole(userId);
		MonsterBeKillBonus bonus = scene.getRoleKillMonsterBonus(role.getId(), bonusId);
		if (bonus == null) {
			return ReplyDomain.BONS_NOT_EXIT;
		}
		ReplyDomain gainMaterialResult = MaterialService.INSTANCE.gainMasteral(user, role, bonus.getBonus());
		if (!gainMaterialResult.isSuccess()) {
			return ReplyDomain.FAILE;
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain putMonsterIntoScene(int userId, int MonsterId) {

		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}

		MonsterDomain monsterDomain = monsterDomainmap.get(MonsterId);

		int uniqueId = IncrementManager.INSTANCE.increase("monster");
		Monster monster = new Monster(uniqueId, scene.getId(), monsterDomain);

		scene.putMonster(monster);
		return ReplyDomain.SUCCESS;
	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		Integer sceneId = sceneUserMap.remove(userLostData.getUser().getUserId());
		if (sceneId == null) {
			return;
		}
		Scene scene = sceneMap.get(sceneId);

		scene.userLeaveScene(userLostData.getUser().getUserId());
		judgeScene(scene);
		System.out.println("用户下线了");
	}

	// 用户切换角色后把场景中的角色移除
	@Event(eventType = EventType.ROLE_CHANGE)
	public void handleRoleChange(EventDealData<RoleChangeData> data) {
		int userId = data.getData().getUserId();

		Integer sceneId = sceneUserMap.remove(userId);
		if(sceneId == null) {
			return;
		}

		Scene scene = sceneMap.get(sceneId);

		Role sceneRole = scene.getSceneUserCache(userId).getRole();
		if (sceneRole.getId() != data.getData().getOldRoleId()) {
			return;
		}

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
			scene.addRoleKillMonsterBonus(monsterDeadData.getKillRoleId(), monsterBeKillBonus);

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

	private void userForceLeaveCopy(int sceneId) {
		Scene scene = sceneMap.get(sceneId);

		// 提醒用户该副本超出了时间限制
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_COPY_BEYOND_TIME);
		scene.notifyAllUser(replyDomain);
		removeScene(scene);
		// 执行移除操作，用户回到上一次所在的地图
		for (SceneUserCache sceneUserCache : scene.getUserMap().values()) {
			sceneUserMap.remove(sceneUserCache.getUserId());
			User user = UserService.INSTANCE.getUser(sceneUserCache.getUserId());
			userJoinScene(user, scene.getSceneTypeId(), sceneUserCache.getLastSceneId());
		}
	}

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