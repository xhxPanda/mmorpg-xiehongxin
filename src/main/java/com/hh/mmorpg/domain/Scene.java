package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.scene.SceneExtension;

public class Scene {

	private int id;
	private String name;
	private List<Integer> neighborSceneIds;

	private ConcurrentHashMap<Integer, User> userMap;

	public Scene(int id, String name, String neighborScenestrs) {
		this.id = id;
		this.name = name;
		this.neighborSceneIds = new ArrayList<Integer>();

		String[] strs = neighborScenestrs.split(",");
		for (String s : strs) {
			neighborSceneIds.add(Integer.parseInt(s));
		}

		this.userMap = new ConcurrentHashMap<Integer, User>();
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
		for (Entry<Integer, User> entry : userMap.entrySet()) {
			if (entry.getValue().getUserId() != userId) {
				SceneExtension.notifyUser(entry.getValue(), userId, cmd);
			}

		}
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

	public ConcurrentHashMap<Integer, User> getUserMap() {
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
