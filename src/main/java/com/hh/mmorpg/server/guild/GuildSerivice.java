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
				if(applyRole.getGuildId() != 0) {
					return ReplyDomain.HAS_IN_GUILD;
				}
				
				// 进入公会缓存
				GuildMember guildMember = new GuildMember(applyRoleId, guildApply.getUserId(), guildApply.getName(),
						role.getLevel(), GuildMemberIdentity.NORMAL_MEMBER.getId(),
						GuildMemberIdentity.NORMAL_MEMBER.getName(), 0, true);
				guild.addNewMember(guildMember);
				
				// 更新角色信息
				RoleDao.INSTANCE.updateRoleGuild(applyRoleId, guildApply.getUserId(), guild.getId());
				
				// 用户在线时notify
				if(UserService.INSTANCE.getUser(guildApply.getUserId()) == null) {
					ReplyDomain notify = new ReplyDomain();
					notify.setStringDomain("公会名称", guild.getName());
					
					GuildExtension.notifyUserJoinGuild(UserService.INSTANCE.getUser(guildApply.getUserId()), notify);
				}
			}
			
			// 最后删除申请
			GuildDao.INSTANCE.deleteApply(applyRoleId, guild.getId());
			guild.removeApply(applyRoleId);
		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

}
