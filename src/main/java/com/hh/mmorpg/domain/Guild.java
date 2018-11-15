package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.jdbc.ResultBuilder;

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

	private ConcurrentHashMap<Integer, GuildMember> guildMemberMap; // 公会成员
	private ConcurrentHashMap<Integer, GuildApply> guildApplyMap; // 公会申请

	public Guild(int id, String name, long guildDonatePoint, String guildDeclaration, int level) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.guildDonatePoint = guildDonatePoint;
		this.guildDeclaration = guildDeclaration;
		this.guildMemberMap = new ConcurrentHashMap<>();
		this.guildApplyMap = new ConcurrentHashMap<>();
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
	 * 添加公会申请
	 * 
	 * @param guildApply
	 */
	public void addApply(GuildApply guildApply) {
		guildApplyMap.put(guildApply.getRoleId(), guildApply);
	}
	
	public void removeApply(int id) {
		guildApplyMap.remove(id);
	}

	/**
	 * 取出申请
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
			return new Guild(id, name, guildDonatePoint, guildDeclaration, level);
		}
	};
}
