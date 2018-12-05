package com.hh.mmorpg.server.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Guild;
import com.hh.mmorpg.domain.GuildMember;
import com.hh.mmorpg.domain.GuildMemberAuthority;
import com.hh.mmorpg.jdbc.JDBCManager;

public class GuildDao {

	public static final GuildDao INSTANCE = new GuildDao();

	private static final String CREAT_GUILD = "INSERT INTO `guild` (`id`, `name`, `guildDeclaration`, `guildDonatePoint`) VALUES (?, ?, ?, ?)";
	private static final String GET_ALL_GUILD = "SELECT * FROM `guild`";
	private static final String DELETE_APPLY = "DELETE FROM guildApply WHERE id = ? AND guildId = ?";
	private static final String DELETE_MEMBER = "DELETE FROM guildApply WHERE roleId = ? AND guildId = ?";
	private static final String INSERT_GUILD_MEMBER = "INSERT INTO `guildmember` (`RoleId`, `GuildId`, `UserId`, `roleName`, `level`, `memberIdentityId`, `contributionPoint`) VALUES (?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_GUILD_MATERIAL = "REPLACE INTO `guildmaterial` (`guildId`, `uniqueId`, `index`, `materialTypeId`, `materialName`, `quantity`) VALUES (?, ?, ?, ?, ?, ?);";
	private static final String DELETE_GUILD_MATERIAL = "delete from `guildmaterial` WHERE guildId = ? AND index = ?";

	private static final String UPDATE_GUILD_AUTHORITY = "REPLACE INTO `guildmemberauthority` (`guildId`, `guildMemberIdentityId`, `canTickMember`, `canSeeBank`, `takeBankMaterialNum`, `canSendPublicMessage`, `canUseGold`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_GUILD_AUTHORITY = "SELECT * FROM `guildmemberauthority` WHERE `guildId` = ?";

	public int creatGuild(Guild guild) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(CREAT_GUILD, new Object[] { guild.getId(),
				guild.getName(), guild.getGuildDeclaration(), guild.getGuildDonatePoint() });
	}

	@SuppressWarnings("unchecked")
	public List<Guild> getAllGuild() {
		try {
			return (List<Guild>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(GET_ALL_GUILD, new Object[] {},
					Guild.BUILDER);
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

	public int insertGuildMember(GuildMember guildMember) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(INSERT_GUILD_MEMBER,
				new Object[] { guildMember.getRoleId(), guildMember.getGuildId(), guildMember.getUserId(),
						guildMember.getRoleName(), guildMember.getMemberIdentityId(),
						guildMember.getContributionPoint() });
	}

	public int insertGuildMaterial(int guildId, int index, BagMaterial bagMaterial) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_GUILD_MATERIAL,
				new Object[] { guildId, bagMaterial.getUniqueId(), index, bagMaterial.getTypeId(),
						bagMaterial.getName(), bagMaterial.getQuantity() });
	}

	public int deleteGuildMaterialIndex(int guildId, int index) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_GUILD_MATERIAL,
				new Object[] { guildId, index });
	}

	public int updateGuildMemberAuthority(GuildMemberAuthority authority) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_GUILD_AUTHORITY,
				new Object[] { authority.getGuildId(), authority.getGuildMemberIdentity().getId(),
						authority.isCanTickMember(), authority.isCanSeeBank(), authority.isCanSendPublicMessage(),
						authority.getCanUseGold() });
	}

	@SuppressWarnings("unchecked")
	public List<GuildMemberAuthority> selectGuildMemberAuthority(int guildId) {
		try {
			return (List<GuildMemberAuthority>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(SELECT_GUILD_AUTHORITY, new Object[] { guildId }, Guild.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<GuildMemberAuthority>();
	}

}
