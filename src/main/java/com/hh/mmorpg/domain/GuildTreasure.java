package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

public class GuildTreasure {

	private int guildId;
	private long quantity;
	private int id;

	public GuildTreasure(int guildId, long quantity, int id) {
		super();
		this.guildId = guildId;
		this.quantity = quantity;
		this.id = id;
	}

	public int getGuildId() {
		return guildId;
	}

	public long getQuantity() {
		return quantity;
	}

	public int getId() {
		return id;
	}

	public static final ResultBuilder<GuildTreasure> BUILDER = new ResultBuilder<GuildTreasure>() {

		@Override
		public GuildTreasure build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub

			int guildId = result.getInt("guildId");
			long quantity = result.getLong("quantity");
			int id = result.getInt("treasureId");

			return new GuildTreasure(guildId, quantity, id);
		}
	};

}
