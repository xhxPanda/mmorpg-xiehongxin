package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;
import com.hh.mmorpg.server.scene.SceneUserCache;
import com.hh.mmorpg.service.user.UserService;

public class Scene {

	private int id;
	private String name;
	private List<Integer> neighborSceneIds;

	private ConcurrentHashMap<Integer, SceneUserCache> userMap;

	private Map<Integer, NpcRole> npcRoleMap;
	private ConcurrentHashMap<Integer, Monster> monsterMap;

	public Scene(int id, String name, String neighborScenestrs) {
		this.id = id;
		this.name = name;
		this.neighborSceneIds = new ArrayList<Integer>();

		String[] strs = neighborScenestrs.split(",");
		for (String s : strs) {
			neighborSceneIds.add(Integer.parseInt(s));
		}

		this.userMap = new ConcurrentHashMap<Integer, SceneUserCache>();
		monsterMap = new ConcurrentHashMap<>();
	}

	public boolean isCanEnter(int id) {
		return neighborSceneIds.contains(id);
	}

	public ReplyDomain userEnterScene(SceneUserCache sceneUserCache) {
		if (userMap.containsKey(sceneUserCache.getUserId())) {
			return ReplyDomain.FAILE;
		}

		userMap.put(sceneUserCache.getUserId(), sceneUserCache);
		notifyOtherUser(sceneUserCache.getUserId(), SceneExtension.NOTIFY_USER_ENTER);
		return ReplyDomain.SUCCESS;
	}

	public SceneUserCache userLeaveScene(User user) {
		SceneUserCache cache = userMap.remove(user.getUserId());
		notifyOtherUser(user.getUserId(), SceneExtension.NOTIFY_USER_LEAVE);
		return cache;
	}

	private void notifyOtherUser(int userId, String cmd) {
		for (Entry<Integer, SceneUserCache> entry : userMap.entrySet()) {
			if (entry.getValue().getUserId() != userId) {
				SceneExtension.notifyUser(UserService.INSTANCE.getUser(entry.getValue().getUserId()), userId, cmd);
			}

		}
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
