package com.hh.mmorpg.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectionDeal {
	
	public static final ConnectionDeal INSTANCE = new ConnectionDeal();

	private Connection conn;

	private ScheduledExecutorService executorService;
	private ConcurrentLinkedQueue<SqlExcutor> blockingQueue;

	private ConnectionDeal() {
		try {
			this.conn = JDBCUtils.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.executorService = Executors.newSingleThreadScheduledExecutor();
		blockingQueue = new ConcurrentLinkedQueue<>();

		start();
	}

	public void excuteObject(String sql, Object[] params) {
		// 入队
		blockingQueue.offer(new SqlExcutor(sql, params));
	}

	public Object excuteObject(String sql, Object[] params, ResultBuilder<?> builder) throws SQLException {
		PreparedStatement pstmt;
		ResultSet result = null;
		try {
			
			pstmt = (java.sql.PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}

			result = pstmt.executeQuery();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return getObject(result, builder);
	}

	public <T> List<T> excuteObjectList(String sql, Object[] params, ResultBuilder<T> builder) throws SQLException {

		PreparedStatement pstmt;
		ResultSet result = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (params != null & params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}

			result = pstmt.executeQuery();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		List<T> list = new ArrayList<T>();

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

			e.printStackTrace();
		}

		return object;
	}

//	private List<Object> getObjectList(ResultSet result, ResultBuilder<?> builder) {
//		List<Object> list = new ArrayList<Object>();
//		try {
//			while (result.next()) {
//				Object object = builder.build(result);
//				list.add(object);
//			}
//		} catch (SQLException e) {
//
//			e.printStackTrace();
//		}
//
//		return list;
//	}

	/**
	 * 内部类，处理sql的元素
	 * 
	 * @author xhx
	 *
	 */
	class SqlExcutor {

		private String sql;
		private Object[] objects;

		public SqlExcutor(String sql, Object[] objects) {
			this.sql = sql;
			this.objects = objects;
		}

		public String getSql() {
			return sql;
		}

		public Object[] getObjects() {
			return objects;
		}

	}

	private void dealSqlIntoDb() {
		if (blockingQueue.isEmpty()) {
			return;
		}
		SqlExcutor sqlExcutor = blockingQueue.poll();
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sqlExcutor.getSql());
			Object[] objects = sqlExcutor.getObjects();
			for (int i = 0; i < objects.length; i++) {
				pstmt.setObject(i + 1, objects[i]);
			}

			pstmt.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	// 数据库心跳
	public void start() {

		executorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				dealSqlIntoDb();
			}
		}, 0, 800, TimeUnit.MILLISECONDS);
	}
}
