package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.jdbc.ResultBuilder;
import com.hh.mmorpg.result.ReplyDomain;
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

	private Map<Integer, BagMaterial> guildBank;
	private Map<Integer, Long> treasureMap;

	private Map<Integer, GuildMemberAuthority> authorityMap;

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

		this.guildBank = new HashMap<>();
		for (int i = 0; i < guildWarehouseCapasity; i++) {
			guildBank.put(i, null);
		}
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

	public void setGuildMember(List<GuildMember> guildMembers) {
		for (GuildMember guildMember : guildMembers) {
			guildMemberMap.put(guildMember.getRoleId(), guildMember);
		}
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
	 * 获取申请
	 * 
	 * @param applyId
	 * @return
	 */
	public GuildApply getApply(int applyId) {
		return guildApplyMap.get(applyId);
	}

	/**
	 * 获取公会某一种财富的数量
	 * 
	 * @param id
	 * @return
	 */
	public long getTreasureNum(int id) {
		return treasureMap.get(id);
	}

	/**
	 * 对公会某一种财富进行存取
	 * 
	 * @param id
	 * @param value
	 */
	public void accessTreasure(int id, long value) {
		Long num = treasureMap.get(id);
		if(num == null) {
			treasureMap.put(id, 0L);
			num = value;
		}
		treasureMap.put(id, num + value);
		GuildDao.INSTANCE.updateGuildTreasure(getId(), id, num + value);
	}

	/**
	 * 存入物品
	 * 
	 * @param bagMaterial
	 * @return
	 */
	public ReplyDomain accessMaterial(BagMaterial bagMaterial) {
		int index = findFirstFreeIndex();

		if (index == -1) {
			return ReplyDomain.BOX_SPACE_NOT_ENOUGH;
		}

		bagMaterial.setRoleId(0); // 抹去所属角色
		bagMaterial.setIndex(index);
		guildBank.put(index, bagMaterial);

		GuildDao.INSTANCE.insertGuildMaterial(id, index, bagMaterial);
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 减少公会仓库中的物品数量
	 * 
	 * @param index
	 * @param num
	 */
	public void decBankMaterial(int index, int num) {
		if (guildBank.get(index).changeQuantity(-num) == 0) {
			guildBank.put(index, null);
			GuildDao.INSTANCE.deleteGuildMaterialIndex(getId(), index);
		} else {
			GuildDao.INSTANCE.insertGuildMaterial(getId(), index, guildBank.get(index));
		}

	}

	/**
	 * 获取公会仓库中某一个格子的物品
	 * 
	 * @param index
	 * @return
	 */
	public BagMaterial getIndexMaterial(int index) {
		return guildBank.get(index);
	}

	/**
	 * 找出公会仓库中第一个空闲的格子
	 * 
	 * @return
	 */
	public int findFirstFreeIndex() {
		for (Entry<Integer, BagMaterial> entry : guildBank.entrySet()) {
			if (entry.getValue() == null) {
				return entry.getKey();
			}
		}

		return -1;
	}

	public GuildMemberAuthority getGuildMemberAuthority(int authorityId) {
		return authorityMap.get(authorityId);
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

	/**
	 * 新增会员
	 * 
	 * @param guildMember
	 */
	public void addNewMember(GuildMember guildMember) {
		guildMemberMap.put(guildMember.getRoleId(), guildMember);
		GuildDao.INSTANCE.insertGuildMember(guildMember);

	}

	public void setguildApply(List<GuildApply> guildApplies) {
		for (GuildApply guildApply : guildApplies) {
			guildApplyMap.put(guildApply.getRoleId(), guildApply);
		}
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

	public int getGuildWarehouseCapasity() {
		return guildWarehouseCapasity;
	}

	public GuildMemberAuthority getAuthorityMap(int authorityId) {
		return authorityMap.get(authorityId);
	}

	public void setAuthorityMap(Map<Integer, GuildMemberAuthority> authorityMap) {
		this.authorityMap = authorityMap;
	}

	public Map<Integer, BagMaterial> getGuildBank() {
		return guildBank;
	}

	/**
	 * 組建公会仓库
	 * 
	 * @param bagMaterial
	 */
	public void putMaterial(BagMaterial bagMaterial) {
		guildBank.put(bagMaterial.getIndex(), bagMaterial);
	}

	/**
	 * 组件公会财富系统
	 * 
	 * @param id
	 * @param quantity
	 */
	public void putGuildTreasure(int id, long quantity) {
		treasureMap.put(id, quantity);
	}

	public String getGuildBankStr() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Integer, BagMaterial> entry : guildBank.entrySet()) {

			if (entry.getValue() == null) {
				continue;
			}
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(entry.getKey()).append(":").append("名称：").append(entry.getValue().getName()).append(" 数量")
					.append(":").append(entry.getValue().getQuantity());
		}
		return builder.toString();
	}

	public String getGuildTreasureStr() {
		StringBuilder builder = new StringBuilder();

		for (UserTreasureType treasureType : UserTreasureType.values()) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append("名称：").append(treasureType.getName()).append(" 数量").append(":")
					.append(treasureMap.get(treasureType.getId()) == null ? 0 : treasureMap.get(treasureType.getId()));
		}
		return builder.toString();
	}

	public Map<Integer, Long> getTreasureMap() {
		return treasureMap;
	}

	public static final ResultBuilder<Guild> BUILDER = new ResultBuilder<Guild>() {
		@Override
		public Guild build(ResultSet result) throws SQLException {
			int id = result.getInt("id");
			String name = result.getString("name");
			int level = result.getInt("level");
			String guildDeclaration = result.getString("guildDeclaration");
			int guildDonatePoint = result.getInt("guildDonatePoint");
			int guildWarehouseCapasity = result.getInt("guildWarehouseCapasity");
			return new Guild(id, name, guildDonatePoint, guildDeclaration, level, guildWarehouseCapasity);
		}
	};

	@Override
	public String toString() {
		return "Guild [名称=" + name + ", 等级=" + level + ", 公会贡献点=" + guildDonatePoint + "]";
	}
}
