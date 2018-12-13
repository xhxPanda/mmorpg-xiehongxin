package com.hh.mmorpg.counter;

/**
 * 数据库计数器，暂时没有用到，这个包可以忽略先
 * 
 * @author xhx
 *
 */
public class CounterService {

	public static final CounterService INSTANCE = new CounterService();

	public void getRoleCounterRecord(int roleId, String key) {
		CounterDao.INSTANCE.getRecord(roleId, key);
	}

	public void updateRoleCounter(int roleId, String key, int record) {
		CounterDao.INSTANCE.updateRecord(key, roleId, record);
	}
}
