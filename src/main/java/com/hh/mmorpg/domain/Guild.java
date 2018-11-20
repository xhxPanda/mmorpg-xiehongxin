package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.server.guild.GuildDao;

/**
 * 
 * @author 公会实体类
 *
 */
public class Guild {

	private int id;
	private String name;
	private int level;
	private long guildDonatePoint; // 公会贡献点数
	private String guildDeclaration; // 公会宣言

	private int guildWarehouseCapasity;

	private ConcurrentHashMap<Integer, GuildMember> guildMemberMap; // 公会成员
	private ConcurrentHashMap<Integer, GuildApply> guildApplyMap; // 公会申请

	private Map<Integer, Map<Integer, BagMaterial>> guildWarehourse;
	private Map<Integer, Integer> treasureMap;

	public Guild(int id, String name, long guildDonatePoint, String guildDeclaration, int level,
			int guildWarehouseCapasity) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.guildDonatePoint = guildDonatePoint;
		this.guildDeclaration = guildDeclaration;
		this.guildMemberMap = new ConcurrentHashMap<>();
		this.guildApplyMap = new ConcurrentHashMap<>();

		this.guildWarehouseCapasity = guildWarehouseCapasity;

		this.guildWarehourse = new HashMap<>();
		this.treasureMap = new HashMap<>();
	}

	public GuildMember getGuildMember(int roleId) {
		return guildMemberMap.get(roleId);
	}

	/**
	 * 获取成员数量
	 * 
	 * @return
	 */
	public int getMemberNum() {
		return guildMemberMap.size();
	}

	/**
	 * 踢用户
	 * 
	 * @param roleId
	 */
	public void removeGuildMember(int roleId) {
		guildMemberMap.remove(roleId);
		GuildDao.INSTANCE.deleteMember(roleId, id);
	}

	/**
	 * 添加公会申请
	 * 
	 * @param guildApply
	 */
	public void addApply(GuildApply guildApply) {
		guildApplyMap.put(guildApply.getRoleId(), guildApply);
	}

	/**
	 * 移除缓存中的申请
	 * 
	 * @param id
	 */
	public void removeApply(GuildApply guildApply) {
		GuildDao.INSTANCE.deleteApply(guildApply.getRoleId(), guildApply.getGuildId());
		guildApplyMap.remove(guildApply.getRoleId());
	}

	/**
	 * 取出申请
	 * 
	 * @param applyId
	 * @return
	 */
	public GuildApply getApply(int applyId) {
		return guildApplyMap.get(applyId);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public long getGuildDonatePoint() {
		return guildDonatePoint;
	}

	public String getGuildDeclaration() {
		return guildDeclaration;
	}

	public Map<Integer, GuildMember> getGuildMemberMap() {
		return guildMemberMap;
	}

	public void addNewMember(GuildMember guildMember) {
		guildMemberMap.put(guildMember.getRoleId(), guildMember);
	}

	public ConcurrentHashMap<Integer, GuildApply> getGuildApplyMap() {
		return guildApplyMap;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public static final ResultBuilder<Guild> BUILDER = new ResultBuilder<Guild>() {
		@Override
		public Guild build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int id = result.getInt("id");
			String name = result.getString("name");
			int level = result.getInt("level");
			String guildDeclaration = result.getString("guildDeclaration");
			int guildDonatePoint = result.getInt("guildDonatePoint");
			int guildWarehouseCapasity = result.getInt("guildWarehouseCapasity");
			return new Guild(id, name, guildDonatePoint, guildDeclaration, level, guildWarehouseCapasity);
		}
	};
}
