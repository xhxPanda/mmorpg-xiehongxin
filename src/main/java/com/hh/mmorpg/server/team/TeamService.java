package com.hh.mmorpg.server.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.hh.mmorpg.domain.OccupationEmun;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.TeamMate;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.JoinTeamData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class TeamService {

	public static final TeamService INSTANCE = new TeamService();

	private static final int MAX_TEAM_CAPACITY = 40; // 最大的队伍人数

	// 队伍缓存
	private ConcurrentHashMap<Integer, Map<Integer, TeamMate>> teamsMap;

	// 邀请列表（就不持久化了）
	private ConcurrentHashMap<Integer, Set<Integer>> applyCache;

	private AtomicInteger teamId;

	private TeamService() {
		this.teamsMap = new ConcurrentHashMap<>();
		this.applyCache = new ConcurrentHashMap<>();

		this.teamId = new AtomicInteger(0);
	}

	public ReplyDomain getTeamInfo(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int teamId = role.getTeamId();
		// 没有队伍
		if (teamId == 0) {
			return ReplyDomain.FAILE;
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

		// 别人已经有队伍了
		Role people = RoleService.INSTANCE.getUserUsingRole(peopleUser.getUserId());
		if (people.getTeamId() != 0) {
			return ReplyDomain.HAS_IN_TEAM;
		}

		Set<Integer> applySet = applyCache.get(people.getId());
		if (applySet != null && applySet.size() > 0) {
			if (applySet.contains(role.getId())) {
				return ReplyDomain.HAS_SENT_APPLY;
			}
		}

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("邀请人", role.getName());
		replyDomain.setIntDomain("邀请人id", role.getId());
		TeamExtension.notifyRole(peopleUser, replyDomain);

		return ReplyDomain.SUCCESS;
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
		if (!isAgree) {
			removeTeamInvition(role.getId(), roleId);
		}

		Set<Integer> applySet = applyCache.get(role.getId());
		// 邀请不存在
		if (!applySet.contains(roleId)) {
			return ReplyDomain.FAILE;
		}

		// 对方不在线就当拒绝了
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			removeTeamInvition(role.getId(), roleId);
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
		Role peopleRole = RoleService.INSTANCE.getUserUsingRole(peopleUser.getUserId());

		Map<Integer, TeamMate> teamMate = null;

		// 新建队伍
		if (peopleRole.getTeamId() == 0) {
			int teamUniqueId = teamId.incrementAndGet();

			teamMate = new HashMap<>();
			teamsMap.put(teamUniqueId, teamMate);

			teamMate.put(peopleRole.getId(), new TeamMate(peopleRole.getId(), peopleRole.getName(),
					OccupationEmun.getOccupationEmun(peopleRole.getOccupationId()), true));
			teamMate.put(role.getId(), new TeamMate(role.getId(), role.getName(),
					OccupationEmun.getOccupationEmun(role.getOccupationId()), true));

			peopleRole.setTeamId(teamUniqueId);

		} else {
			teamMate = teamsMap.get(peopleRole.getTeamId());

			// 判断队伍是否满人了
			if (teamMate.size() >= MAX_TEAM_CAPACITY) {
				return ReplyDomain.TEAM_FULL;
			}

			teamMate.put(role.getId(), new TeamMate(role.getId(), role.getName(),
					OccupationEmun.getOccupationEmun(role.getOccupationId()), true));
		}

		role.setTeamId(peopleRole.getTeamId());

		JoinTeamData joinTeamData = new JoinTeamData(role, new ArrayList<>(teamMate.values()));
		EventHandlerManager.INSATNCE.methodInvoke(EventType.JOIN_GUILD, new EventDealData<JoinTeamData>(joinTeamData));

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain quitTeam(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		int teamId = role.getTeamId();

		if (role.getTeamId() == 0) {
			return ReplyDomain.FAILE;
		}

		// 移除用户
		Map<Integer, TeamMate> team = teamsMap.get(teamId);

		role.setTeamId(0);
		notifyOneTeamMateQuit(team, role.getName());

		// 如果队伍中已经没人了，就解散队伍
		if (team.size() == 0) {
			teamsMap.remove(teamId);
		}
		return ReplyDomain.SUCCESS;
	}

	private void removeTeamInvition(int roleId, int inviteId) {
		Set<Integer> applySet = applyCache.get(roleId);
		if (applySet == null || applySet.size() == 0) {
			return;
		}
		if (applySet.contains(inviteId))
			applySet.remove(inviteId);
	}

	private void notifyOneTeamMateQuit(Map<Integer, TeamMate> team, String name) {
		for (TeamMate teamMate : team.values()) {
			if (!RoleService.INSTANCE.isOnline(teamMate.getRoleId())) {
				continue;
			}
			User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(teamMate.getRoleId()));
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("名称", name);

			TeamExtension.notifyRole(peopleUser, replyDomain);
		}
	}

	// 用户下线，把他的状态设为下线
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();

		Role role = userLostData.getRole();

		int teamId = role.getTeamId();

		if (teamId == 0) {
			return;
		}

		TeamMate teamMate = teamsMap.get(teamId).get(role.getId());
		teamMate.setOnline(false);
		
		MaterialService.INSTANCE.persistenceRoleMatetrial(role);
		System.out.println("删除用户缓存使用角色");
	}
}
