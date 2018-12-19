package com.hh.mmorpg.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtils {
	private static DataSource dataSource = null;
	static {
		// dataSource资源只能初始化一次
		dataSource = new ComboPooledDataSource("part0");
	}

	public static void releaseConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
	
	public static DataSource getDataSource() {
        return dataSource;
    }
}