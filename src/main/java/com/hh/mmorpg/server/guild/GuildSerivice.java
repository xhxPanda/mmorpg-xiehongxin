package com.hh.mmorpg.server.guild;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Guild;
import com.hh.mmorpg.domain.GuildApply;
import com.hh.mmorpg.domain.GuildMember;
import com.hh.mmorpg.domain.GuildMemberAuthority;
import com.hh.mmorpg.domain.GuildMemberIdentity;
import com.hh.mmorpg.domain.GuildTreasure;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.GuildJoinData;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleDao;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.service.user.UserService;

public class GuildSerivice {

	public static final GuildSerivice INSTANCE = new GuildSerivice();

	// 热点数据，用户角色数据缓存
	ConcurrentHashMap<Integer, Guild> guildCache;

	// 角色创建公会最小等级
	private static final int DEFAULT_CREAT_LEVEL = 10;
	// 默认公会宣言
	private static final String DEFAULT_GUILD_DECLARATION = "我们是最强的";
	// 创建公会需要消耗的财物
	public static final String DEC_CREAT_GUILD_MATERIAL = "3:2:1000";
	// 公会仓库默认容量
	public static final int DEFAULT_WAREHOUSE_CAPACITY = 20;

	// 公会满人指标
	private static final int GUILD_FULL_NUM = 500;

	private ReentrantLock lock;

	private GuildSerivice() {
		guildCache = new ConcurrentHashMap<>();

		List<Guild> guildList = GuildDao.INSTANCE.getAllGuild();
		for (Guild guild : guildList) {
			guildCache.put(guild.getId(), guild);
			assemblingGuild(guild);
		}
		this.lock = new ReentrantLock();

		EventHandlerManager.INSATNCE.register(this);
	}

	/**
	 * 展示公会基本资料
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showGuildInfo(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int guildId = role.getGuildId();
		if (guildId == 0) {
			return ReplyDomain.NOT_IN_GUILD;
		}

		Guild guild = guildCache.get(guildId);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("公会信息", guild.toString());
		replyDomain.setIntDomain("人数", guild.getMemberNum());

		return replyDomain;
	}

	/**
	 * 展示所有的公会申请
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showGuildApply(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int guildId = role.getGuildId();
		if (guildId == 0) {
			return ReplyDomain.NOT_IN_GUILD;
		}
		Guild guild = guildCache.get(guildId);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("公会申请列表", guild.getGuildApplyMap().values());
		return replyDomain;
	}

	/**
	 * 展示公会所有的成员
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showGuildMember(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int guildId = role.getGuildId();

		if (guildId == 0) {
			return ReplyDomain.NOT_IN_GUILD;
		}

		Guild guild = guildCache.get(guildId);
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("公会成员列表", guild.getGuildMemberMap().values());
		return replyDomain;
	}

	/**
	 * 创建公会
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain creatGuild(User user, String name, String declaration) {


		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (name == null) {
			return ReplyDomain.FAILE;
		}

		// 等级不足
		if (role.getLevel() < DEFAULT_CREAT_LEVEL) {
			return ReplyDomain.LEVEL_NOT_ENOUGH;
		}

		// 已经有公会了
		if (role.getGuildId() != 0) {
			return ReplyDomain.HAD_HAS_GUILD;
		}

		// 扣除创建公会的消费
		ReplyDomain decDomain = MaterialService.INSTANCE.decMasterial(user, role, DEC_CREAT_GUILD_MATERIAL);
		if (!decDomain.isSuccess()) {
			return ReplyDomain.FAILE;
		}

		if (declaration == null) {
			declaration = DEFAULT_GUILD_DECLARATION;
		}

		// 创建公会并加入缓存
		int guildId = IncrementManager.INSTANCE.increase("guild");
		Guild guild = new Guild(guildId, name, 0, declaration, 1, DEFAULT_WAREHOUSE_CAPACITY);
		guildCache.put(guild.getId(), guild);

		GuildDao.INSTANCE.creatGuild(guild);

		// 会长是第一个会员
		GuildMember guildMember = new GuildMember(role.getId(), user.getUserId(), role.getName(), role.getLevel(),
				GuildMemberIdentity.PRESIDENT.getId(), GuildMemberIdentity.PRESIDENT.getName(), 0, true, guildId);
		guild.addNewMember(guildMember);

		// 给自己设置公会id
		role.setGuildId(guildId);

		// 抛出角色进入公会事件
		GuildJoinData guildJoinData = new GuildJoinData(role, guild.getId());
		EventHandlerManager.INSATNCE.methodInvoke(EventType.JOIN_GUILD,
				new EventDealData<GuildJoinData>(guildJoinData));

		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);

		return replyDomain;
	}

	/**
	 * 发送请求加入公会申请
	 * 
	 * @param user
	 * @param guildId
	 * @param content
	 * @return
	 */
	public ReplyDomain sendGuildApply(User user, int guildId, String content) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() != 0) {
			return ReplyDomain.HAD_HAS_GUILD;
		}

		Guild guild = guildCache.get(guildId);
		if (guild == null) {
			return ReplyDomain.FAILE;
		}

		// 不能重复发送
		if (guild.getApply(role.getId()) != null) {
			return ReplyDomain.HAS_SENT_APPLY;
		}
		if (guild.getMemberNum() >= GUILD_FULL_NUM) {
			return ReplyDomain.GUILD_FULL;
		}

		GuildApply guildApply = new GuildApply(role.getId(), user.getUserId(), role.getName(), guildId, content);
		guild.addApply(guildApply);
		GuildDao.INSTANCE.insertApply(guildApply);
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 处理申请
	 * 
	 * @param user
	 * @param applyId
	 * @param isAggre
	 * @return
	 */
	public ReplyDomain eaminationApply(User user, int applyId, boolean isAggre) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}
		Guild guild = guildCache.get(role.getGuildId());
		if (guild == null) {
			return ReplyDomain.FAILE;
		}

		// 副会长以上的权限才能处理申请
		GuildMember myMember = guild.getGuildMember(role.getId());
		if (myMember.getMemberIdentityId() < GuildMemberIdentity.VICE_PRESIDENT.getId()) {
			return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
		}

		// 在这里加锁，缩小锁的粒度
		lock.tryLock();
		try {

			// 判断是否满人
			if (guild.getMemberNum() >= GUILD_FULL_NUM) {
				return ReplyDomain.GUILD_FULL;
			}

			GuildApply guildApply = guild.getApply(applyId);
			if (guildApply == null) {
				return ReplyDomain.FAILE;
			}

			int applyRoleId = guildApply.getRoleId();
			if (isAggre) {
				Role applyRole = RoleService.INSTANCE.getUserRole(guildApply.getUserId(), applyRoleId);
				if (applyRole.getGuildId() != 0) {
					return ReplyDomain.HAS_IN_GUILD;
				}

				// 进入公会缓存
				GuildMember guildMember = new GuildMember(applyRoleId, guildApply.getUserId(), guildApply.getName(),
						role.getLevel(), GuildMemberIdentity.NORMAL_MEMBER.getId(),
						GuildMemberIdentity.NORMAL_MEMBER.getName(), 0, true, guild.getId());
				guild.addNewMember(guildMember);

				updateRoleGuildStatus(guild.getId(), applyRole.getId(), applyRole.getUserId());
				// 用户在线时notify
				if (RoleService.INSTANCE.isOnline(applyRoleId)) {
					ReplyDomain notify = new ReplyDomain();
					notify.setStringDomain("公会名称", guild.getName());
					notify.setStringDomain("cmd", GuildExtension.NOTIFY_USER_JOIN_GUILD);

					GuildExtension.notifyUser(UserService.INSTANCE.getUser(guildApply.getUserId()), notify);
				}

				// 抛出角色进入公会事件
				GuildJoinData guildJoinData = new GuildJoinData(applyRole, guild.getId());
				EventHandlerManager.INSATNCE.methodInvoke(EventType.JOIN_GUILD,
						new EventDealData<GuildJoinData>(guildJoinData));
			}

			// 最后删除申请
			guild.removeApply(guildApply);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 踢出公会
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain tickOutRole(User user, int roleId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());

		// 副会长以上的权限才能够踢人
		GuildMember guildMember = guild.getGuildMember(role.getId());
		if (guildMember.getMemberIdentityId() < GuildMemberIdentity.VICE_PRESIDENT.getId()) {
			return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
		}

		lock.tryLock();
		try {

			GuildMember tickOutGuildMember = guild.getGuildMember(roleId);
			if (tickOutGuildMember == null) {
				return ReplyDomain.FAILE;
			}

			// 平权跟权限不高的不能踢
			if (tickOutGuildMember.getMemberIdentityId() >= guildMember.getMemberIdentityId()) {
				return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
			}

			guild.removeGuildMember(roleId);

			updateRoleGuildStatus(0, roleId, tickOutGuildMember.getUserId());
			// 用户在线时notify
			if (RoleService.INSTANCE.isOnline(roleId)) {
				ReplyDomain notify = new ReplyDomain();
				notify.setStringDomain("公会名称", guild.getName());
				notify.setStringDomain("cmd", GuildExtension.NOTIFY_USER_TICK_OUT);

				GuildExtension.notifyUser(UserService.INSTANCE.getUser(tickOutGuildMember.getUserId()), notify);
			}
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 主动退出公会
	 * 
	 * @return
	 */
	public ReplyDomain leaveGuild(User user) {
		lock.tryLock();
		try {
			Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

			if (role.getGuildId() == 0) {
				return ReplyDomain.FAILE;
			}

			Guild guild = guildCache.get(role.getGuildId());

			GuildMember guildMember = guild.getGuildMember(role.getId());

			// 会长不能主动退出
			if (guildMember.getMemberIdentityId() == GuildMemberIdentity.PRESIDENT.getId()) {
				return ReplyDomain.FAILE;
			}

			// 移除角色
			guild.removeGuildMember(role.getId());
			updateRoleGuildStatus(guild.getId(), role.getId(), role.getUserId());

		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 转让公会
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain transferGuild(User user, int roleId) {
		lock.tryLock();
		try {
			Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

			if (role.getGuildId() == 0) {
				return ReplyDomain.FAILE;
			}

			Guild guild = guildCache.get(role.getGuildId());

			GuildMember guildMember = guild.getGuildMember(role.getId());
			if (guildMember.getMemberIdentityId() != GuildMemberIdentity.PRESIDENT.getId()) {
				return ReplyDomain.GUILD_AUTHORITY;
			}

			// 只能转给公会中的人
			GuildMember toGuildMember = guild.getGuildMember(roleId);
			if (toGuildMember == null) {
				return ReplyDomain.FAILE;
			}

			guildMember.setMemberIdentityId(GuildMemberIdentity.NORMAL_MEMBER.getId());
			toGuildMember.setMemberIdentityId(GuildMemberIdentity.PRESIDENT.getId());

			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", GuildExtension.NOTIFY_PRESIDENT_CHANGE);
			replyDomain.setStringDomain("新会长", toGuildMember.getRoleName());
			notifyAllMember(role.getGuildId(), replyDomain);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 公会捐献物品
	 * 
	 * @param user
	 * @param index
	 * @return
	 */
	public ReplyDomain donateMaterial(User user, int index, int num) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());
//		GuildMember guildMember = guild.getGuildMember(role.getId());
//		GuildMemberAuthority authority = guild.getAuthorityMap(guildMember.getMemberIdentityId());
//		if (!authority.isCanSeeBank()) {
//			return ReplyDomain.GUILD_AUTHORITY;
//		}

		BagMaterial bagMaterial = role.getBagMaterialIndex(index);
		if (bagMaterial == null) {
			return ReplyDomain.FAILE;
		}
		if (bagMaterial.getQuantity() < num) {
			return ReplyDomain.NOT_ENOUGH;
		}

		BagMaterial guildMaterial = role.decMaterialIndex(index, num);
		ReplyDomain domain = guild.accessMaterial(guildMaterial);

		return domain;
	}

	/**
	 * 捐献财富类的物品
	 * 
	 * @param user
	 * @param id
	 * @param num
	 * @return
	 */
	public ReplyDomain donateTreasure(User user, int id, int num) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());
//		GuildMember guildMember = guild.getGuildMember(role.getId());

//		GuildMemberAuthority authority = guild.getAuthorityMap(guildMember.getMemberIdentityId());
//		if (!authority.isCanSeeBank()) {
//			return ReplyDomain.GUILD_AUTHORITY;
//		}

		UserTreasure userTreasure = role.getRoleTreasure(id);
		if (userTreasure.getQuantity() < num) {
			return ReplyDomain.NOT_ENOUGH;
		}

		lock.lock();
		try {

			UserTreasure treasure = role.getRoleTreasure(id);
			if (treasure.getQuantity() < num) {
				return ReplyDomain.NOT_ENOUGH;
			}
			treasure.changeQuantity(-num);

			guild.accessTreasure(id, num);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 获取公会仓库中的物品
	 * 
	 * @param user
	 * @param index
	 * @param num
	 * @return
	 */
	public ReplyDomain extractMaterial(User user, int index, int num) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());

		// 副会长以上的职位才能领取
		GuildMember guildMember = guild.getGuildMember(role.getId());
		if (guildMember.getMemberIdentityId() <= GuildMemberIdentity.VICE_PRESIDENT.getId()) {
			return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
		}

		lock.lock();
		try {
			BagMaterial material = guild.getIndexMaterial(index);
			if (material == null || material.getQuantity() < num) {
				return ReplyDomain.NOT_ENOUGH;
			}
			// 减去物品
			guild.decBankMaterial(index, num);
			BagMaterial bagMaterial = new BagMaterial(material, role.getId(), num);

			role.addMaterial(bagMaterial);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 公会财富提取
	 * 
	 * @param role
	 * @param id
	 * @param num
	 * @return
	 */
	public ReplyDomain guildReimbursement(User user, int id, int num) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());
		lock.lock();

		// 副会长以上的职位才能领取
		GuildMember guildMember = guild.getGuildMember(role.getId());
		if (guildMember.getMemberIdentityId() <= GuildMemberIdentity.VICE_PRESIDENT.getId()) {
			return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
		}

//		GuildMemberAuthority authority = guild.getAuthorityMap(guildMember.getMemberIdentityId());
//		if (!authority.isCanSeeBank()) {
//			return ReplyDomain.GUILD_AUTHORITY;
//		}
//
//		int dayCanUseGold = authority.getCanUseGold();
//		int count = CounterService.INSTANCE.getRoleCounterRecord(role.getId(), CounterKey.GET_GUILD_TREASURE);
//		if (count + num > dayCanUseGold) {
//			return ReplyDomain.NOT_ENOUGH;
//		}

		try {
			long guildNum = guild.getTreasureNum(id);
			if (guildNum < num) {
				return ReplyDomain.NOT_ENOUGH;
			}

			guild.accessTreasure(id, -num);
			MaterialService.INSTANCE.gainMasteral(user, role, "3:" + id + ":" + num);
//			CounterService.INSTANCE.updateRoleCounter(role.getId(), CounterKey.GET_GUILD_TREASURE, count + num);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 更新角色公会信息
	 * 
	 * @param guildId
	 * @param roleId
	 * @param userId
	 */
	private void updateRoleGuildStatus(int guildId, int roleId, int userId) {
		Role role = RoleService.INSTANCE.getUserRole(userId, roleId);
		role.setGuildId(guildId);

		// 更新角色信息
		RoleDao.INSTANCE.updateRoleGuild(roleId, userId, guildId);
	}

	/**
	 * 查看公会仓库
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain showGuildBank(User user) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getGuildId() == 0) {
			return ReplyDomain.FAILE;
		}

		Guild guild = guildCache.get(role.getGuildId());
//		GuildMember guildMember = guild.getGuildMember(role.getId());

//		GuildMemberAuthority authority = guild.getAuthorityMap(guildMember.getMemberIdentityId());
//		if (!authority.isCanSeeBank()) {
//			return ReplyDomain.GUILD_AUTHORITY;
//		}

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("公会物品", guild.getGuildBankStr());
		replyDomain.setStringDomain("公会财富", guild.getGuildTreasureStr());
		return replyDomain;
	}

	/**
	 * 把有关公会的信息都缓存
	 * 
	 * @param guild
	 */
	private void assemblingGuild(Guild guild) {
		int guildId = guild.getId();
		List<GuildApply> guildApplies = GuildDao.INSTANCE.getAllGuildApply(guildId);
		List<GuildMember> guildMembers = GuildDao.INSTANCE.getGuildMembers(guildId);
		List<BagMaterial> guildMaterial = GuildDao.INSTANCE.selectGuildMaterial(guildId);

		List<GuildMemberAuthority> guildMemberAuthorities = GuildDao.INSTANCE.selectGuildMemberAuthority(guildId);
		Map<Integer, GuildMemberAuthority> authorityMap = guildMemberAuthorities.stream()
				.collect(Collectors.toMap(GuildMemberAuthority::getGuildMemberIdentityId, a -> a));

		List<GuildTreasure> treasures = GuildDao.INSTANCE.selectGuildTreasure(guildId);

		guild.setguildApply(guildApplies);
		guild.setGuildMember(guildMembers);
		guild.setAuthorityMap(authorityMap);

		for (BagMaterial bagMaterial : guildMaterial) {
			guild.putMaterial(bagMaterial);
		}

		for (GuildTreasure guildTreasure : treasures) {
			guild.putGuildTreasure(guildTreasure.getId(), guildTreasure.getQuantity());
		}
	}

	/**
	 * 通知所有成员
	 * 
	 * @param guildId
	 * @param domain
	 */
	private void notifyAllMember(int guildId, ReplyDomain domain) {
		Guild guild = guildCache.get(guildId);
		if (guild == null)
			return;

		for (GuildMember member : guild.getGuildMemberMap().values()) {
			int userId = member.getUserId();
			User user = UserService.INSTANCE.getUser(userId);
			if (user == null) {
				continue;
			}

			GuildExtension.notifyUser(user, domain);
		}
	}

	/**
	 * 改变公会成员的在线状态
	 * 
	 * @param data
	 */
	@Event(eventType = EventType.ROLE_CHANGE)
	public void handleChangeRole(EventDealData<RoleChangeData> data) {
		RoleChangeData roleChangeData = data.getData();

		Role oldRole = roleChangeData.getOldRole();
		if (oldRole != null && oldRole.getGuildId() != 0) {
			Guild guild = guildCache.get(oldRole.getGuildId());
			guild.getGuildMember(oldRole.getId()).setOnline(false);
		}

		Role newRole = roleChangeData.getNewRole();
		if (newRole.getGuildId() != 0) {
			Guild newGuild = guildCache.get(newRole.getGuildId());
			newGuild.getGuildMember(newRole.getId()).setOnline(true);
		}

	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();

		Role role = userLostData.getRole();

		if (role.getGuildId() == 0) {
			return;
		}

		Guild guild = guildCache.get(role.getGuildId());
		guild.getGuildMember(role.getId()).setOnline(false);

		System.out.println("删除用户缓存使用角色");
	}
}
