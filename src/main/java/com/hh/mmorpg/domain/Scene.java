package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;
import com.hh.mmorpg.server.scene.SceneUserCache;
import com.hh.mmorpg.service.user.UserService;

public class Scene {

	private int id;
	private String name;
	private List<Integer> neighborSceneIds;

	private static ConcurrentHashMap<Integer, SceneUserCache> userMap = new ConcurrentHashMap<>();

	private Map<Integer, NpcRole> npcRoleMap;
	private ConcurrentHashMap<Integer, Monster> monsterMap;

	private ScheduledExecutorService executorService;

	public Scene(int id, String name, String neighborScenestrs) {
		this.id = id;
		this.name = name;
		this.neighborSceneIds = new ArrayList<Integer>();

		String[] strs = neighborScenestrs.split(",");
		for (String s : strs) {
			neighborSceneIds.add(Integer.parseInt(s));
		}
		this.monsterMap = new ConcurrentHashMap<>();
		this.executorService = Executors.newSingleThreadScheduledExecutor();
		start();
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
		notifyOtherUser(sceneUserCache.getUserId(), domain);
		return ReplyDomain.SUCCESS;
	}

	public SceneUserCache userLeaveScene(User user) {
		SceneUserCache cache = userMap.remove(user.getUserId());
		ReplyDomain domain = new ReplyDomain();
		domain.setStringDomain("cmd", SceneExtension.NOTIFY_USER_LEAVE);
		notifyOtherUser(user.getUserId(), domain);
		return cache;
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

	public ConcurrentHashMap<Integer, Monster> getMonsterMap() {
		return monsterMap;
	}

	public void setNpcRoleMap(Map<Integer, NpcRole> npcRoleMap) {
		this.npcRoleMap = npcRoleMap;
	}

	public void setMonsterMap(Map<Integer, Monster> monsterMap) {
		this.monsterMap.putAll(monsterMap);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getNeighborSceneIds() {
		return neighborSceneIds;
	}

	public ConcurrentHashMap<Integer, SceneUserCache> getUserMap() {
		return userMap;
	}

	public void start() {
		// 场景心跳
		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (Monster monster : monsterMap.values()) {
					if (monster.isDead()) {
						long now = System.currentTimeMillis();
						if (now - monster.getBeKilledTime() < monster.getFreshTime()) {
							continue;
						} else {

						}
					} else {
						monster.takeEffect();
					}

				}

				for (SceneUserCache cache : userMap.values()) {
					cache.getRole().takeEffect();

					// 加红蓝
					cache.getRole().effectAttribute(3, 2);
					cache.getRole().effectAttribute(4, 2);

					ReplyDomain domain = new ReplyDomain();
					domain.setStringDomain("cmd", SceneExtension.NOTIFT_USER_ATTRIBUATE_CHANGE);
					notifyAllUser(domain);

				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public void shutdown() {
		executorService.shutdown();
	}

	public static final ResultBuilder<Scene> BUILDER = new ResultBuilder<Scene>() {
		@Override
		public Scene build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int id = result.getInt(1);
			String name = result.getString(2);
			String neighborScenestrs = result.getString(3);
			return new Scene(id, name, neighborScenestrs);
		}
	};
}
