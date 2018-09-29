package com.hh.mmorpg.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class ConnectionDeal {
	
	private Connection conn;
	
	public ConnectionDeal(Connection conn) {
		this.conn = conn;
	}

	public int excuteObject(String sql, Object[] params) {
		PreparedStatement pstmt;
		int result = 1;
		 try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			for(int i = 0;i<params.length;i++) {
				pstmt.setObject(i, params[i]);
			}
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return result;
	}
	
	public Object excuteObject(String sql, Object[] params, ResultBuilder<?> builder) throws SQLException {
		 PreparedStatement pstmt;
		 ResultSet result = null;
		 try {
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
				for(int i = 0;i<params.length;i++) {
					pstmt.setObject(i+1, params[i]);
				}
				
				result = pstmt.executeQuery();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 return builder.build(result);
	}
	
}
