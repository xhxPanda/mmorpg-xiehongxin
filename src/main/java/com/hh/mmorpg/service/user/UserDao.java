package com.hh.mmorpg.service.user;

import java.sql.SQLException;

import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.jdbc.JDBCManager;

public class UserDao {

	public static final UserDao INSTANCE = new UserDao();

	private static final String SELECT_USER = "SELECT * FROM user0 WHERE userId = ? AND password = ?";
	private static final String INSERT_USER = "INSERT INTO user0(`userId`,`password`, `name`) values (?, ?, ?)";

	public User selectUser(int userId, String password) {
		User user = null;
		try {
			user = (User) JDBCManager.INSTANCE.getConn("part0").excuteObject(SELECT_USER,
					new Object[] { userId, password }, User.BUILDER);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public void insertUser(User user) {
		JDBCManager.INSTANCE.getConn("part0").excuteObject(INSERT_USER,
				new Object[] { user.getUserId(), user.getPassword(), user.getName() });
	}
}
