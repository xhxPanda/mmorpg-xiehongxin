package com.hh.mmorpg.server.role;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;

public class RoleService {

	public static final RoleService INSTANCE = new RoleService();

	private ConcurrentHashMap<Integer, Map<Integer, Role>> userAllRoleMap;

	private ConcurrentHashMap<Integer, Role> userRoleMap;

	private RoleService() {
		userAllRoleMap = new ConcurrentHashMap<Integer, Map<Integer, Role>>();
		userRoleMap = new ConcurrentHashMap<>();
	}

//	public ReplyDomain getAllRole(User user) {
//		// TODO Auto-generated method stub
//		int userId = user.getUserId();
//
//		return null;
//	}

	public ReplyDomain userUseRole(User user, int roleId) {
		int userId = user.getUserId();
		Role role = getUserRole(userId, roleId);
		if (role == null) {
			return ReplyDomain.FAILE;
		}

		return ReplyDomain.SUCCESS;
	}

	public Role getUserUsingRole(User user) {
		return userRoleMap.get(user.getUserId());
	}

	private Role getUserRole(int userId, int roleId) {
		return getUserAllRole(userId).get(roleId);
	}

	private Map<Integer, Role> getUserAllRole(int userId) {
		if (userAllRoleMap.get(userId) == null) {
			List<Role> roles = RoleDao.INSTANCE.selectUserRole(userId);
			Map<Integer, Role> map = roles.stream().collect(Collectors.toMap(Role::getId, a -> a));
			userAllRoleMap.put(userId, map);
		}

		Map<Integer, Role> roleMap = userAllRoleMap.get(userId);
		return roleMap;
	}
}
