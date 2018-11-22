package com.hh.mmorpg.server.team;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class TeamService {

	public static final TeamService INSTANCE = new TeamService();

	private static final int MAX_TEAM_CAPACITY = 40; // 最大的队伍人数

	private ConcurrentHashMap<Integer, Set<Integer>> teamsMap;
	private ConcurrentHashMap<Integer, Set<Integer>> applyCache;

	private AtomicInteger teamId;

	private TeamService() {
		this.teamsMap = new ConcurrentHashMap<>();
		this.applyCache = new ConcurrentHashMap<>();

		this.teamId = new AtomicInteger(0);
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

		// 对方断线了
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			removeTeamInvition(role.getId(), roleId);
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
		Role peopleRole = RoleService.INSTANCE.getUserUsingRole(peopleUser.getUserId());

		// 新建队伍
		if (peopleRole.getTeamId() == 0) {
			int teamUniqueId = teamId.incrementAndGet();

			Set<Integer> teamMate = new HashSet<>();
			teamsMap.put(teamUniqueId, teamMate);

			teamMate.add(peopleRole.getId());
			teamMate.add(role.getId());

			peopleRole.setTeamId(teamUniqueId);
		} else {
			Set<Integer> teamMate = teamsMap.get(peopleRole.getTeamId());

			// 判断队伍是否满人了
			if (teamMate.size() >= MAX_TEAM_CAPACITY) {
				return ReplyDomain.TEAM_FULL;
			}

			teamMate.add(role.getId());
		}

		role.setTeamId(peopleRole.getTeamId());
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain quitTeam(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		if (role.getTeamId() == 0) {
			return ReplyDomain.FAILE;
		}

		// 移除用户
		Set<Integer> team = teamsMap.get(role.getTeamId());
		team.remove(role.getId());

		role.setTeamId(0);
		notifyOneTeamMateQuit(team, role.getName());
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

	private void notifyOneTeamMateQuit(Set<Integer> team, String name) {
		for (Integer roleId : team) {
			if (!RoleService.INSTANCE.isOnline(roleId)) {
				continue;
			}
			User peopleUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("名称", name);

			TeamExtension.notifyRole(peopleUser, replyDomain);
		}
	}
}
