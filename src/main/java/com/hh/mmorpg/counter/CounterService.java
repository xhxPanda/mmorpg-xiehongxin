package com.hh.mmorpg.counter;

/**
 * 数据库计数器
 * 
 * @author xhx
 *
 */
public class CounterService {

	public static final CounterService INSTANCE = new CounterService();

	public int getRoleCounterRecord(int roleId, String key) {
		return CounterDao.INSTANCE.getRecord(roleId, key);
	}

	public void updateRoleCounter(int roleId, String key, int record) {
		CounterDao.INSTANCE.updateRecord(key, roleId, record);
	}
}