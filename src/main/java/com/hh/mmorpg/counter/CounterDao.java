package com.hh.mmorpg.counter;

import com.hh.mmorpg.jdbc.JDBCManager;

public class CounterDao {

	public static final CounterDao INSTANCE = new CounterDao();

	private static final String SELECT_COUNT = "SELECT record FROM WHERE roleId = ? AND key = ?";
	
	private static final String UPDATE_RECORD = "INSERT INTO `counter` (`key`, `roleId`, `record`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE record = ?";

	public void getRecord(int roleId, String key) {
		JDBCManager.INSTANCE.getConn("part0").excuteObject(SELECT_COUNT, new Object[] { roleId, key });
	}
	
	public void updateRecord(String key, int roleId, int record) {
		JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_RECORD, new Object[] { key, roleId, record });
	}

}
