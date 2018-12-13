package com.hh.mmorpg.Increment;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.jdbc.JDBCManager;

public class IncrementDao {

	public static final IncrementDao INSTANCE = new IncrementDao();

	private IncrementDao() {

	}

	private static final String GET_INCREMENT_DOMAIN = "SELECT * FROM increment";

	private static final String UPDATE_INCREMENT_DOMAIN = "UPDATE increment SET now = ? WHERE name = ?";

	@SuppressWarnings("unchecked")
	public List<IncrementDomain> getAllIncrementDomain() {
		try {
			return (List<IncrementDomain>) JDBCManager.INSTANCE.getConn("work").excuteObjectList(GET_INCREMENT_DOMAIN,
					new Object[] {}, IncrementDomain.BUILDER);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateIncrementDomain(int now, String name) {
		JDBCManager.INSTANCE.getConn("work").excuteObject(UPDATE_INCREMENT_DOMAIN, new Object[] { now, name });
	}

}
