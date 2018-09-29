package com.hh.mmorpg.domain;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;

public class Scene {

	private int id;
	private String name;
	private Map<Integer, NpcRole> npcRoleList;
	private Map<Integer, Monster> monsterList;
	private List<Integer> neighborSceneIds;

	private ConcurrentHashMap<Integer, User> userMap;

	public Scene() {
		userMap = new ConcurrentHashMap<Integer, User>();
	}

	public boolean isCanEnter(int id) {
		return neighborSceneIds.contains(id);
	}

	public ReplyDomain userEnterScene(User user) {
		if (userMap.containsKey(user.getUserId())) {
			return ReplyDomain.FAILE;
		}
		userMap.put(user.getUserId(), user);
		notifyOtherUser(user.getUserId(), SceneExtension.NOTIFY_USER_ENTER);
		return ReplyDomain.SUCCESS;
	}
	
	public void userLeaveScene(User user) {
		userMap.remove(user.getUserId());
		notifyOtherUser(user.getUserId(), SceneExtension.NOTIFY_USER_LEAVE);
	}
	
	private void notifyOtherUser(int userId, String cmd) {
		for(Entry<Integer, User> entry : userMap.entrySet()) {
			SceneExtension.notifyUserEnter(entry.getValue(), cmd);
		}
	}
}
