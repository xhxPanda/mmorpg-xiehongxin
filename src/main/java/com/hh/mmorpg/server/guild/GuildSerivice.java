package com.hh.mmorpg.server.guild;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Guild;
import com.hh.mmorpg.domain.GuildApply;
import com.hh.mmorpg.domain.GuildLevelDomain;
import com.hh.mmorpg.domain.GuildMember;
import com.hh.mmorpg.domain.GuildMemberIdentity;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
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

	// 创建公会最小等级
	private static final int DEFAULT_CREAT_LEVEL = 10;
	// 默认公会宣言
	private static final String DEFAULT_GUILD_DECLARATION = "我们是最强的";
	// 创建公会消耗的财物
	public static final String DEC_CREAT_GUILD_MATERIAL = "3:2:1000";

	private Map<Integer, GuildLevelDomain> guildLevelDomainMap; // 公会等级限制

	private ReentrantLock lock;

	private GuildSerivice() {
		guildCache = new ConcurrentHashMap<>();

		List<Guild> guildList = GuildDao.INSTANCE.getAllGuild();
		for (Guild guild : guildList) {
			guildCache.put(guild.getId(), guild);
		}

		this.guildLevelDomainMap = GuildLevelXmlResolutionMenager.INSTANCE.resolution();
		this.lock = new ReentrantLock();
	}

	/**
	 * 创建公会
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain creatGuild(User user, String name, String declaration) {
		// TODO Auto-generated method stub

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
		Guild guild = new Guild(guildId, name, 0, declaration, 1);
		guildCache.put(guild.getId(), guild);

		GuildMember guildMember = new GuildMember(role.getId(), user.getUserId(), role.getName(), role.getLevel(),
				GuildMemberIdentity.PRESIDENT.getId(), GuildMemberIdentity.PRESIDENT.getName(), 0, true);
		guild.addNewMember(guildMember);

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
		if (guild.getApply(role.getRoleId()) != null) {
			return ReplyDomain.HAS_SENT_APPLY;
		}

		GuildLevelDomain guildLevelDomain = guildLevelDomainMap.get(guild.getLevel());
		if (guildLevelDomain.getCanJoinMemberNum() <= guild.getMemberNum()) {
			return ReplyDomain.GUILD_FULL;
		}

		GuildApply guildApply = new GuildApply(role.getId(), user.getUserId(), role.getName(), guildId, content);
		guild.addApply(guildApply);
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
		lock.tryLock();
		try {
			Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

			if (role.getGuildId() == 0) {
				return ReplyDomain.FAILE;
			}
			Guild guild = guildCache.get(role.getGuildId());
			if (guild == null) {
				return ReplyDomain.FAILE;
			}

			GuildApply guildApply = guild.getApply(applyId);
			if (guildApply == null) {
				return ReplyDomain.FAILE;
			}

			int applyRoleId = guildApply.getGuildId();
			if (isAggre) {
				Role applyRole = RoleService.INSTANCE.getUserRole(guildApply.getUserId(), applyRoleId);
				if (applyRole.getGuildId() != 0) {
					return ReplyDomain.HAS_IN_GUILD;
				}

				// 进入公会缓存
				GuildMember guildMember = new GuildMember(applyRoleId, guildApply.getUserId(), guildApply.getName(),
						role.getLevel(), GuildMemberIdentity.NORMAL_MEMBER.getId(),
						GuildMemberIdentity.NORMAL_MEMBER.getName(), 0, true);
				guild.addNewMember(guildMember);

				updateRoleGuildStatus(guild.getId(), applyRole.getId(), applyRole.getUserId());
				// 用户在线时notify
				if (UserService.INSTANCE.getUser(guildApply.getUserId()) == null) {
					ReplyDomain notify = new ReplyDomain();
					notify.setStringDomain("公会名称", guild.getName());
					notify.setStringDomain("cmd", GuildExtension.NOTIFY_USER_JOIN_GUILD);

					GuildExtension.notifyUser(UserService.INSTANCE.getUser(guildApply.getUserId()), notify);
				}
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
		lock.tryLock();
		try {
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

			GuildMember tickOutGuildMember = guild.getGuildMember(roleId);
			if (tickOutGuildMember == null) {
				return ReplyDomain.FAILE;
			}

			// 平权跟权限不高的不能踢
			if (tickOutGuildMember.getMemberIdentityId() <= guildMember.getMemberIdentityId()) {
				return ReplyDomain.LACK_OF_GUILD_JURISDICTION;
			}

			guild.removeGuildMember(roleId);

			updateRoleGuildStatus(0, roleId, tickOutGuildMember.getUserId());
			// 用户在线时notify
			if (UserService.INSTANCE.getUser(tickOutGuildMember.getUserId()) == null) {
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
				return ReplyDomain.FAILE;
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
		RoleDao.INSTANCE.updateRoleGuild(guildId, roleId, userId);
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
}
