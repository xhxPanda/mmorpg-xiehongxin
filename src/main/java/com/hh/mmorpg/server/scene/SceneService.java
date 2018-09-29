package com.hh.mmorpg.server.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;

public class SceneService {

	public static final SceneService INSTANCE = new SceneService();

	private Map<Integer, Scene> sceneMap;

	private ConcurrentHashMap<Integer, Integer> sceneUserMap;

	// 错误码
	private static final int CAN_NOT_ENTER = 4;

	private SceneService() {
		sceneUserMap = new ConcurrentHashMap<Integer, Integer>();

		sceneMap = new HashMap<Integer, Scene>();
	}

	public ReplyDomain userJoinScene(User user, int oldSceneId, int sceneId) {
		int userId = user.getUserId();
		if (sceneUserMap.get(userId) != oldSceneId) {
			return ReplyDomain.ILLEGAL;
		}

		Scene scene = sceneMap.get(oldSceneId);
		if (!scene.isCanEnter(sceneId)) {
			return new ReplyDomain(CAN_NOT_ENTER);
		}

		// 离开旧场景
		scene.userLeaveScene(user);
		
		// 进入新场景
		Scene newScene = sceneMap.get(sceneId);
		
		if(newScene.userEnterScene(user).isSuccess()) {
			sceneUserMap.put(userId, sceneId);
		}
		
		return ReplyDomain.SUCCESS;
	}
}
