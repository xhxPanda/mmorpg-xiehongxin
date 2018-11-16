package com.hh.mmorpg.server.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hh.mmorpg.domain.Guild;
import com.hh.mmorpg.jdbc.JDBCManager;

public class GuildDao {
	
	public static final GuildDao INSTANCE = new GuildDao();

	private static final String CREAT_GUILD = "INSERT INTO `guild` (`id`, `name`, `guildDeclaration`, `guildDonatePoint`) VALUES (?, ?, ?, ?)";
	private static final String GET_ALL_GUILD = "SELECT * FROM `guild`";
	private static final String DELETE_APPLY = "DELETE FROM guildApply WHERE id = ? AND guildId = ?";
	private static final String DELETE_MEMBER = "DELETE FROM guildApply WHERE roleId = ? AND guildId = ?";
	
	public int creatGuild(Guild guild) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(CREAT_GUILD, new Object[] { guild.getId(),
				guild.getName(), guild.getGuildDeclaration(), guild.getGuildDonatePoint() });
	}
	
	@SuppressWarnings("unchecked")
	public List<Guild> getAllGuild() {
		try {
			return (List<Guild>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(GET_ALL_GUILD,
					new Object[] { }, Guild.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Guild>();
	}
	
	public int deleteApply(int roleId, int guildId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_APPLY, new Object[] { roleId, guildId });
	}
	
	public int deleteMember(int roleId, int guildId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_MEMBER, new Object[] { roleId, guildId });
	}
	
	
}
