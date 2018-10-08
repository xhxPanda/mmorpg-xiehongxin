package com.hh.mmorpg.server.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UserLostData;
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

		List<Scene> scenes = SceneDao.INSTANCE.getScene();
		for (Scene scene : scenes) {
			sceneMap.put(scene.getId(), scene);
		}

		EventHandlerManager.INSATNCE.register(this);
	}

	public ReplyDomain userJoinScene(User user, int sceneId) {
		int userId = user.getUserId();

		Integer oldSceneId = sceneUserMap.get(userId);
		if (oldSceneId != null) {
			Scene scene = sceneMap.get(oldSceneId);
			if (!scene.isCanEnter(sceneId)) {
				return new ReplyDomain(CAN_NOT_ENTER);
			}
			scene.userLeaveScene(user);
		}

		// 进入新场景
		Scene newScene = sceneMap.get(sceneId);

		if (newScene.userEnterScene(user).isSuccess()) {
			sceneUserMap.put(userId, sceneId);
		}

		return ReplyDomain.SUCCESS;
	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		int sceneId = sceneUserMap.get(userLostData.getUser().getUserId());
		Scene scene = sceneMap.get(sceneId);

		scene.userLeaveScene(userLostData.getUser());

		System.out.println("用户下线了");
	}
}
