package com.hh.mmorpg.Increment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class IncrementDomain {

	private String name;
	private int step;
	private AtomicInteger now;

	public IncrementDomain(String name, int step, int now) {
		this.name = name;
		this.step = step;
		this.now = new AtomicInteger(now);
	}

	public String getName() {
		return name;
	}

	public int getStep() {
		return step;
	}

	public AtomicInteger getNow() {
		return now;
	}

	public static final ResultBuilder<IncrementDomain> BUILDER = new ResultBuilder<IncrementDomain>() {
		@Override
		public IncrementDomain build(ResultSet result) throws SQLException {
			String name = result.getString(1);
			int step = result.getInt(2);
			int now = result.getInt(3);
			return new IncrementDomain(name, step, now);
		}
	};
}
