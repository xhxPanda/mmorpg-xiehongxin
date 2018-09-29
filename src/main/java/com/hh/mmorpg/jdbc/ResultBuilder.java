package com.hh.mmorpg.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultBuilder<T> {
	
	public abstract T build(ResultSet result) throws SQLException;

}
