package com.hh.mmorpg.server.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hh.mmorpg.domain.OccupationEmun;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.TeamMate;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.JoinTeamData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleDao;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

/**
 * 处理队伍相关
 * @author xhx
 *
 */
public class TeamService {

	public static final TeamService INSTANCE = new TeamService();

	private static final int MAX_TEAM_CAPACITY = 40; // 最大的队伍人数

	// 队伍缓存
	private ConcurrentHashMap<Integer, Map<Integer, TeamMate>> teamsMap;

	// 邀请列表（就不持久化了）
	private ConcurrentHashMap<Integer, Set<Integer>> applyCache;

	private AtomicInteger teamId;

	// 锁
	private Lock lock;

	private TeamService() {
		this.teamsMap = new ConcurrentHashMap<>();
		this.applyCache = new ConcurrentHashMap<>();

		this.teamId = new AtomicInteger(0);

		this.lock = new ReentrantLock();
		
		// 注册事件
		EventHandler.INSTANCE.addHandler(UserLostData.class, userLostEvent);
	}

	public ReplyDomain getTeamInfo(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int teamId = role.getTeamId();
		// 没有队伍
		if (teamId == 0) {
			return ReplyDomain.NOT_IN_TEAM;
		}

		Map<Integer, TeamMate> teamMateIds = teamsMap.get(teamId);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("队友", teamMateIds.values());
		return replyDomain;
	}

	/**
	 * 邀请组队
	 * 
	 * @param user
	 * @param userId
	 * @return
	 */
	public ReplyDomain inviteRoleOrganizeTeam(User user, int roleId) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));

		Scene myScene = SceneService.INSTANCE.getUserScene(user.getUserId());
		Scene peopleScene = SceneService.INSTANCE.getUserScene(peopleUser.getUserId());

		// 不在同一个场景
		if (myScene.getId() != peopleScene.getId()) {
			return ReplyDomain.NOT_IN_SCENE;
		}

		// 别人已在队伍中
		Role people = RoleService.INSTANCE.getUserUsingRole(peopleUser.getUserId());
		if (people.getTeamId() != 0) {
			return ReplyDomain.HAS_IN_TEAM;
		}

		// 已经发过邀请了
		Set<Integer> applySet = applyCache.get(people.getId());
		if (applySet != null && applySet.size() > 0) {
			if (applySet.contains(role.getId())) {
				return ReplyDomain.HAS_SENT_APPLY;
			}
		}

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("组队邀请人", role.getName());
		replyDomain.setIntDomain("邀请人id", role.getId());
		TeamExtension.notifyRole(peopleUser, replyDomain);

		addApply(role.getId(), roleId);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 添加队伍申请缓存
	 * 
	 * @param roleId
	 * @param beRoleId
	 */
	private void addApply(int roleId, int beRoleId) {

		Set<Integer> set = applyCache.get(beRoleId);
		if (set == null) {
			set = new HashSet<>();
			applyCache.put(beRoleId, set);
		}
		set.add(roleId);
	}

	/**
	 * 处理组队邀请的请求
	 * 
	 * @param user
	 * @param roleId
	 * @param isAgree
	 * @return
	 */
	public ReplyDomain dealTeamApply(User user, int roleId, boolean isAgree) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		// 无论怎样都先移除申请缓存
		removeTeamInvition(role.getId(), roleId);

		if (!isAgree) {
			return ReplyDomain.SUCCESS;
		}

		Set<Integer> applySet = applyCache.get(roleId);
		// 邀请不存在
		if (applySet == null || !applySet.contains(role.getId())) {
			return ReplyDomain.FAILE;
		}

		// 对方不在线就当拒绝了
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		lock.lock();
		try {
			User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
			Role peopleRole = RoleService.INSTANCE.getUserUsingRole(peopleUser.getUserId());

			Map<Integer, TeamMate> teamMate = null;

			// 新建队伍
			if (peopleRole.getTeamId() == 0) {
				int teamUniqueId = teamId.incrementAndGet();

				teamMate = new HashMap<>();
				teamsMap.put(teamUniqueId, teamMate);

				// 邀请人成为队长
				teamMate.put(peopleRole.getId(),
						new TeamMate(peopleRole.getId(), peopleRole.getUserId(), peopleRole.getName(),
								OccupationEmun.getOccupationEmun(peopleRole.getOccupationId()), true, true));

				// 被邀请人成为队员
				teamMate.put(role.getId(), new TeamMate(role.getId(), role.getUserId(), role.getName(),
						OccupationEmun.getOccupationEmun(role.getOccupationId()), true, false));

				peopleRole.setTeamId(teamUniqueId);

			} else {
				teamMate = teamsMap.get(peopleRole.getTeamId());

				// 判断队伍是否满人了
				if (teamMate.size() >= MAX_TEAM_CAPACITY) {
					return ReplyDomain.TEAM_FULL;
				}

				// 只能成为队员
				teamMate.put(role.getId(), new TeamMate(role.getId(), role.getUserId(), role.getName(),
						OccupationEmun.getOccupationEmun(role.getOccupationId()), true, false));
			}

			role.setTeamId(peopleRole.getTeamId());

			JoinTeamData joinTeamData = new JoinTeamData(role, new ArrayList<>(teamMate.values()));
			EventHandler.INSTANCE.invodeMethod(JoinTeamData.class, joinTeamData);

			// 提醒双方组队成功
			TeamExtension.notifyRole(peopleUser, getTeamInfo(peopleUser));
			TeamExtension.notifyRole(user, getTeamInfo(user));

		} finally {
			lock.unlock();
		}
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 主动退出队伍
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain quitTeam(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getTeamId() == 0) {
			return ReplyDomain.FAILE;
		}

		Map<Integer, TeamMate> team = teamsMap.get(role.getTeamId());

		// 判断是否队长
		TeamMate teamMate = team.get(role.getId());
		if (teamMate.isTeamLeader() && team.size() > 1) {
			return ReplyDomain.FAILE;
		}

		lock.lock();

		try {
			// 重置队伍状态
			role.setTeamId(0);

			team.remove(role.getUniqueId());
			// 如果队伍中已经没人了，就解散队伍
			if (team.size() == 0) {
				teamsMap.remove(role.getTeamId());
			} else {
				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("cmd", TeamExtension.NOTIFY_TEAM_MATE_QUIT);
				replyDomain.setStringDomain("名称", role.getName());
				notifyAllTeamMate(team, replyDomain);

			}

		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 转让队长权限
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain transferCaptain(User user, int roleId) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getTeamId() == 0) {
			return ReplyDomain.FAILE;
		}

		Map<Integer, TeamMate> team = teamsMap.get(role.getTeamId());
		if (!team.containsKey(roleId)) {
			return ReplyDomain.FAILE;
		}

		team.get(role.getId()).setTeamLeader(false);
		team.get(roleId).setTeamLeader(false);

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("新任队长名称", team.get(roleId).getName());
		replyDomain.setIntDomain("新任队长id", team.get(roleId).getRoleId());
		notifyAllTeamMate(team, replyDomain);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 踢人
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain tickTeamMate(User user, int roleId) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getTeamId() == 0) {
			return ReplyDomain.FAILE;
		}

		Map<Integer, TeamMate> team = teamsMap.get(role.getTeamId());

		if (!team.get(role.getId()).isTeamLeader()) {
			return ReplyDomain.IS_NOT_TEAM_LEADER;
		}

		if (!team.containsKey(roleId)) {
			return ReplyDomain.FAILE;
		}

		lock.lock();
		try {
			team.remove(roleId);

			Role peopleRole = RoleService.INSTANCE.getUserRole(team.get(role.getId()).getUserId(), roleId);
			peopleRole.setTeamId(0);
			if (!RoleService.INSTANCE.isOnline(roleId)) {
				// 如果不在线，修改数据库中他的队伍信息
				RoleDao.INSTANCE.updateRoleTeam(roleId, 0);
			} else {
				User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setStringDomain("cmd", TeamExtension.NOTIFY_BE_TICKED);
				replyDomain.setStringDomain("名称", team.get(role.getId()).getName());
				TeamExtension.notifyRole(peopleUser, replyDomain);
			}

			team.remove(roleId);

		} finally {
			lock.unlock();
		}

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 获取团队信息
	 * 
	 * @param teamId
	 * @return
	 */
	public Map<Integer, TeamMate> getTeam(int teamId) {
		return teamsMap.get(teamId);
	}

	/**
	 * 移除申请
	 * 
	 * @param roleId
	 * @param inviteId
	 */
	private void removeTeamInvition(int roleId, int inviteId) {
		Set<Integer> applySet = applyCache.get(roleId);
		if (applySet == null || applySet.size() == 0) {
			return;
		}
		if (applySet.contains(inviteId))
			applySet.remove(inviteId);
	}

	/**
	 * 向队伍广播信息
	 * 
	 * @param team
	 * @param replyDomain
	 */
	private void notifyAllTeamMate(Map<Integer, TeamMate> team, ReplyDomain replyDomain) {
		for (TeamMate teamMate : team.values()) {
			if (!RoleService.INSTANCE.isOnline(teamMate.getRoleId())) {
				continue;
			}
			User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(teamMate.getRoleId()));
			TeamExtension.notifyRole(peopleUser, replyDomain);
		}
	}

	// 处理队员离线的业务
	private EventBuilder<UserLostData> userLostEvent = new EventBuilder<UserLostData>() {

		@Override
		public void handler(UserLostData userLostData) {

			Role role = userLostData.getRole();

			int teamId = role.getTeamId();

			if (teamId == 0) {
				return;
			}

			TeamMate teamMate = teamsMap.get(teamId).get(role.getId());

			List<TeamMate> onlineTeamMate = new ArrayList<>();
			for (TeamMate mate : teamsMap.get(teamId).values()) {
				if (!mate.isOnline()) {
					onlineTeamMate.add(mate);
				}
			}
			// 队伍中没有人在线了，移除该队伍
			if (onlineTeamMate.size() == 0) {
				teamsMap.remove(teamId);
				return;
			}

			// 如果他是队长，就需要转交队长角色给别人
			if (teamMate.isTeamLeader()) {
				teamMate.setTeamLeader(false);
				onlineTeamMate.get(0).setOnline(true);
			}

			teamMate.setOnline(false);
		}
	};
	

}
