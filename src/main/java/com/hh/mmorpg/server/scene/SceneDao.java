package com.hh.mmorpg.server.scene;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.jdbc.JDBCManager;

public class SceneDao {

	public static final SceneDao INSTANCE = new SceneDao();

	private static final String SELECT_SCENE = "SELECT * FROM scene";

	public List<Scene> getScene() {
		try {
			@SuppressWarnings("unchecked")
			List<Scene> scenes = (List<Scene>) JDBCManager.INSTANCE.getConn("work").excuteObjectList(SELECT_SCENE,
					new Object[] {}, Scene.BUILDER);
			return scenes;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
