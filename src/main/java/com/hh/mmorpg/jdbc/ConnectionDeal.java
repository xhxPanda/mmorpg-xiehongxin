package com.hh.mmorpg.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
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
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}

			result = pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getObject(result, builder);
	}

	public List<?> excuteObjectList(String sql, Object[] params, ResultBuilder<?> builder) throws SQLException {

		PreparedStatement pstmt;
		ResultSet result = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if(params != null & params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			
			result = pstmt.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Object> list = new ArrayList<Object>();

		while (result.next()) {
			list.add(builder.build(result));
		}
		return list;
	}

	private Object getObject(ResultSet result, ResultBuilder<?> builder) {
		Object object = null;
		try {
			while (result.next()) {
				object = builder.build(result);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return object;
	}

	@SuppressWarnings("unused")
	private List<Object> getObjectList(ResultSet result, ResultBuilder<?> builder) {
		List<Object> list = new ArrayList<Object>();
		try {
			while (result.next()) {
				Object object = builder.build(result);
				list.add(object);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
