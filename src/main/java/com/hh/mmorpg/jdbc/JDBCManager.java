package com.hh.mmorpg.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.mysql.jdbc.Connection;

public class JDBCManager {

	public static final JDBCManager INSTANCE = new JDBCManager();

	private Map<String, String> dbUrl;
	private String userName = "root";
	private String password = "123456";

	private JDBCManager() {
		dbUrl = new HashMap<String, String>();

		dbUrl.put("part0", "jdbc:mysql://127.0.0.1:3306/part0");
		dbUrl.put("work", "jdbc:mysql://127.0.0.1:3306/work");
	}

	public ConnectionDeal getConn(String name) {
		String driver = "com.mysql.jdbc.Driver";
		String url = dbUrl.get(name);
		Connection conn = null;
		try {
			Class.forName(driver); 
			conn = (Connection) DriverManager.getConnection(url, userName, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ConnectionDeal connectionDeal = new ConnectionDeal(conn);
		return connectionDeal;
	}
}
