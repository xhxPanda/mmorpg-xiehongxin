package com.hh.mmorpg.server.guild;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Guild;
import com.hh.mmorpg.domain.GuildApply;
import com.hh.mmorpg.domain.GuildMember;
import com.hh.mmorpg.domain.GuildMemberAuthority;
import com.hh.mmorpg.domain.GuildTreasure;
import com.hh.mmorpg.jdbc.JDBCManager;

public class GuildDao {

	public static final GuildDao INSTANCE = new GuildDao();

	private static final String CREAT_GUILD = "INSERT INTO `guild` (`id`, `name`, `guildDeclaration`, `guildDonatePoint`) VALUES (?, ?, ?, ?)";
	private static final String GET_ALL_GUILD = "SELECT * FROM `guild`";

	private static final String INSERT_APPLY = "INSERT INTO `guildapply` (`guildId`, `RoleId`, `userId`, `name`, `content`) VALUES (?, ?, ?, ?, ?)";
	private static final String DELETE_APPLY = "DELETE FROM `guildapply` WHERE RoleId = ? AND guildId = ?";
	private static final String SELECT_APPLY = "SELECT * FROM `guildapply` WHERE `GuildId` = ?";

	private static final String DELETE_MEMBER = "DELETE FROM guildApply WHERE roleId = ? AND guildId = ?";
	private static final String INSERT_GUILD_MEMBER = "INSERT INTO `guildmember` (`RoleId`, `GuildId`, `UserId`, `roleName`, `level`, `memberIdentityId`, `contributionPoint`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_GUILD_MEMBER = "SELECT * FROM `guildmember` WHERE `GuildId` = ?";

	private static final String UPDATE_GUILD_MATERIAL = "REPLACE INTO `guildmaterial` (`guildId`, `uniqueId`, `index`, `id`, `materialTypeId`, `name`, `quantity`, `sellPrice`, `roleId`, `typeId`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String DELETE_GUILD_MATERIAL = "delete from `guildmaterial` WHERE `guildId` = ? AND `index` = ?";
	private static final String SELECT_GUILD_MATERIAL = "SELECT * FROM `guildmaterial` WHERE guildId = ?";

	private static final String UPDATE_GUILD_AUTHORITY = "REPLACE INTO `guildmemberauthority` (`guildId`, `guildMemberIdentityId`, `canTickMember`, `canSeeBank`, `takeBankMaterialNum`, `canSendPublicMessage`, `canUseGold`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_GUILD_AUTHORITY = "SELECT * FROM `guildmemberauthority` WHERE `guildId` = ?";

	private static final String SELECT_GUILD_TREASURE = "SELECT * FROM `guildtreasure` WHERE `guildId` = ?";
	private static final String UPDATE_GUILD_TREASURE = "REPLACE INTO `guildtreasure` (`guildId`, `treasureId`, `quantity`) VALUES (?, ?, ?)";

	/**
	 * 创建公会
	 * 
	 * @param guild
	 * @return
	 */
	public void creatGuild(Guild guild) {
		JDBCManager.INSTANCE.getConn("part0").excuteObject(CREAT_GUILD,
				new Object[] { guild.getId(), guild.getName(), guild.getGuildDeclaration(), guild.getGuildDonatePoint(),
						guild.getLevel(), guild.getGuildWarehouseCapasity() });
	}

	/**
	 * 获取所有公会
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Guild> getAllGuild() {
		try {
			return (List<Guild>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(GET_ALL_GUILD, new Object[] {},
					Guild.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<Guild>();
	}

	/**
	 * 增加公会申请
	 * 
	 * @param apply
	 * @return
	 */
	public void insertApply(GuildApply apply) {
		JDBCManager.INSTANCE.getConn("part0").excuteObject(INSERT_APPLY, new Object[] { apply.getGuildId(),
				apply.getRoleId(), apply.getUserId(), apply.getName(), apply.getContent() });
	}

	/**
	 * 获取公会申请
	 * 
	 * @param guildId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GuildApply> getAllGuildApply(int guildId) {
		try {
			return (List<GuildApply>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_APPLY,
					new Object[] { guildId }, GuildApply.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<GuildApply>();
	}

	/**
	 * 删除公会申请
	 * 
	 * @param roleId
	 * @param guildId
	 * @return
	 */
	public void deleteApply(int roleId, int guildId) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_APPLY, new Object[] { roleId, guildId });
	}

	/**
	 * 获取所有的公会成员
	 * 
	 * @param guildId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GuildMember> getGuildMembers(int guildId) {
		try {
			return (List<GuildMember>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_GUILD_MEMBER,
					new Object[] { guildId }, GuildMember.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<GuildMember>();
	}

	/**
	 * 删除公会申请
	 * 
	 * @param roleId
	 * @param guildId
	 * @return
	 */
	public void deleteMember(int roleId, int guildId) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_MEMBER, new Object[] { roleId, guildId });
	}

	/**
	 * 新增公会成员
	 * 
	 * @param guildMember
	 * @return
	 */
	public void insertGuildMember(GuildMember guildMember) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(INSERT_GUILD_MEMBER,
				new Object[] { guildMember.getRoleId(), guildMember.getGuildId(), guildMember.getUserId(),
						guildMember.getRoleName(), guildMember.getLevel(), guildMember.getMemberIdentityId(),
						guildMember.getContributionPoint() });
	}

	/**
	 * 新增公会物品
	 * 
	 * @param guildId
	 * @param index
	 * @param bagMaterial
	 * @return
	 */
	public void insertGuildMaterial(int guildId, int index, BagMaterial bagMaterial) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_GUILD_MATERIAL,
				new Object[] { guildId, bagMaterial.getUniqueId(), index, bagMaterial.getId(), bagMaterial.getTypeId(),
						bagMaterial.getName(), bagMaterial.getQuantity(), bagMaterial.getSellPrice(),
						bagMaterial.getRoleId(), bagMaterial.getTypeId() });
	}

	/**
	 * 删除公会物品
	 * 
	 * @param guildId
	 * @param index
	 * @return
	 */
	public void deleteGuildMaterialIndex(int guildId, int index) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_GUILD_MATERIAL,
				new Object[] { guildId, index });
	}

	/**
	 * 更新权限
	 * 
	 * @param authority
	 * @return
	 */
	public void updateGuildMemberAuthority(GuildMemberAuthority authority) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_GUILD_AUTHORITY,
				new Object[] { authority.getGuildId(), authority.getGuildMemberIdentityId(),
						authority.isCanTickMember(), authority.isCanSeeBank(), authority.isCanSendPublicMessage(),
						authority.getCanUseGold() });
	}

	/**
	 * 获取公会身份权限
	 * 
	 * @param guildId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GuildMemberAuthority> selectGuildMemberAuthority(int guildId) {
		try {
			return (List<GuildMemberAuthority>) JDBCManager.INSTANCE.getConn("part0")
					.excuteObjectList(SELECT_GUILD_AUTHORITY, new Object[] { guildId }, Guild.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<GuildMemberAuthority>();
	}

	/**
	 * 获取公会仓库的物品
	 * 
	 * @param guildId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BagMaterial> selectGuildMaterial(int guildId) {
		try {
			return (List<BagMaterial>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_GUILD_MATERIAL,
					new Object[] { guildId }, BagMaterial.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<BagMaterial>();
	}

	/**
	 * 获取公会财富
	 * 
	 * @param guildId
	 * @param treasureId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<GuildTreasure> selectGuildTreasure(int guildId) {
		try {
			return (List<GuildTreasure>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_GUILD_TREASURE,
					new Object[] { guildId }, GuildTreasure.BUILDER);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return new ArrayList<GuildTreasure>();
	}

	public void updateGuildTreasure(int guildId, int id, long quantity) {
		 JDBCManager.INSTANCE.getConn("part0").excuteObject(UPDATE_GUILD_TREASURE,
				new Object[] { guildId, id, quantity });
	}
}
