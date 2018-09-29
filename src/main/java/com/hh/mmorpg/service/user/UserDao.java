package com.hh.mmorpg.service.user;

import java.sql.SQLException;

import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.jdbc.JDBCManager;

public class UserDao {

	public static final UserDao INSTANCE = new UserDao();

	private static final int DB_INDEX = 1000 * 10000;

	private static final String SELECT_USER = "SELECT * FROM user%s WHERE userId = ? AND password = ?";
	private static final String INSERT_USER = "INSERT user%s('userId','password') values (?, ?)";

	public User selectUser(int userId, String password) {
		int dbIndex = userId / DB_INDEX;
		User user = null;
		try {
			user = (User) JDBCManager.INSTANCE.getConn("part" + dbIndex)
					.excuteObject(String.format(SELECT_USER, dbIndex), new Object[] { userId, password }, User.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return user;
	}

	public int insertUser(User user) {
		int dbIndex = user.getUserId() / DB_INDEX;
		return JDBCManager.INSTANCE.getConn("part" + dbIndex).excuteObject(String.format(INSERT_USER, dbIndex),
				new Object[] { user.getUserId(), user.getPassword() });
	}
}
