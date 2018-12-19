package com.hh.mmorpg.server.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.OccupationEmun;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleDomain;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.event.data.UpdateLevelData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.equiment.UserEquimentService;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.mission.MissionDao;
import com.hh.mmorpg.server.mission.MissionService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.server.skill.SkillService;
import com.hh.mmorpg.server.team.TeamService;
import com.hh.mmorpg.service.user.UserService;

public class RoleService {

	public static final RoleService INSTANCE = new RoleService();

	private Map<Integer, RoleDomain> roleDomainMap;
	// 默认格子数量
	private static final int DEFAULT_CAPACITY = 20;

	private ConcurrentHashMap<Integer, Integer> roleToUser;

	// 热点数据，用户角色数据缓存
	private LoadingCache<Integer, Map<Integer, Role>> cache = CacheBuilder.newBuilder()
			.refreshAfterWrite(10, TimeUnit.MINUTES).expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(1000)
			.build(new CacheLoader<Integer, Map<Integer, Role>>() {
				@Override
				/** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
				public Map<Integer, Role> load(Integer appKey) {
					return getUserAllRole(appKey);
				}
			});

	private ConcurrentHashMap<Integer, Role> userRoleMap;

	private RoleService() {
		userRoleMap = new ConcurrentHashMap<>();
		roleDomainMap = RoleXmlResolutionManager.INSTANCE.resolution();

		this.roleToUser = new ConcurrentHashMap<>();

		// 注册事件
		EventHandler.INSTANCE.addHandler(UpdateLevelData.class, levelUpEvent);
		EventHandler.INSTANCE.addHandler(UserLostData.class, userLostEvent);
	}

	/**
	 * 获取用户所有的角色
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain getAllRole(User user) {

		int userId = user.getUserId();

		Map<Integer, Role> map = getUserAllRole(userId);
		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setListDomain("角色列表", map.values());
		return replyDomain;
	}

	/**
	 * 转职
	 * 
	 * @param user
	 * @param occupationId
	 * @return
	 */
	public ReplyDomain transferOccupation(User user, int occupationId) {

		OccupationEmun occupationEmun = OccupationEmun.getOccupationEmun(occupationId);
		if (occupationEmun == null || occupationEmun.getId() == OccupationEmun.NONE.getId()) {
			return ReplyDomain.FAILE;
		}

		Role role = getUserUsingRole(user.getUserId());
		role.setOccupationId(occupationId);

		return null;
	}

	/**
	 * 使用角色
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain userUseRole(User user, int roleId) {

		if (user == null) {
			return ReplyDomain.FAILE;
		}

		int userId = user.getUserId();
		Role oldRole = userRoleMap.get(userId);

		// 使用了相同的角色
		if (oldRole != null && oldRole.getId() == roleId) {
			return ReplyDomain.SUCCESS;
		}

		Role role = getUserRole(userId, roleId);
		if (role == null) {
			return ReplyDomain.FAILE;
		}

		userRoleMap.put(userId, role);

		if (oldRole != null) {
			// 删除缓存
			roleToUser.remove(oldRole.getId());
			roleToUser.put(roleId, userId);
		}

		// 抛出替换角色的事件
		RoleChangeData data = new RoleChangeData(userId, oldRole, role);
		EventHandler.INSTANCE.invodeMethod(RoleChangeData.class, data);

		roleToUser.put(roleId, userId);
		ReplyDomain replyDomain = new ReplyDomain("使用角色" + ResultCode.SUCCESS);

		SceneService.INSTANCE.userJoinLastCopy(role);
		return replyDomain;
	}

	/**
	 * 获取角色userId，可以用来判断角色是否在线
	 * 
	 * @param roleId
	 * @return
	 */
	public boolean isOnline(int roleId) {
		Integer userId = roleToUser.get(roleId);
		if (userId == null) {
			return false;
		}
		
		if (SceneService.INSTANCE.getUserScene(userId) == null) {
			return false;
		}

		return true;
	}

	/**
	 * 获取用户userid
	 * 
	 * @param roleId
	 * @return
	 */
	public Integer getUserId(int roleId) {
		return roleToUser.get(roleId);
	}

	/**
	 * 获取用户当前角色
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain getUserUsingRole(User user) {
		ReplyDomain replyDomain = new ReplyDomain();
		Role role = getUserUsingRole(user.getUserId());
		if (role != null) {
			replyDomain.setStringDomain("当前角色", role.toString());
		} else {
			replyDomain.setStringDomain("当前角色", "");
		}
		return replyDomain;
	}

	/**
	 * 获取用户当前角色
	 * 
	 * @param userId
	 * @return
	 */
	public Role getUserUsingRole(int userId) {
		Role role = userRoleMap.get(userId);
		return role;
	}

	/**
	 * 创建角色
	 * 
	 * @param user
	 * @param tribeId
	 * @param name
	 * @return
	 */
	public ReplyDomain creatRole(User user, int occupationId, String name) {

		int id = IncrementManager.INSTANCE.increase("role");

		RoleDomain domain = roleDomainMap.get(occupationId);

		Role role = new Role(user.getUserId(), id, name, occupationId, DEFAULT_CAPACITY, 1, 0, 0, 0,
				domain.getAttributeStr(), 0);
		RoleDao.INSTANCE.insertRole(role);

		assemblingRole(role);

		// 令角色接收所有成就类型的任务
		MissionService.INSTANCE.acceptAchievemnetMission(role);

		// 更新缓存
		getUserAllRole(user.getUserId()).put(role.getId(), role);

		// 创建角色以后显示用户所拥有的所有角色
		return getAllRole(user);
	}

	/**
	 * 获取初始的角色信息
	 * 
	 * @param roleId
	 * @return
	 */
	public RoleDomain getRoleDomain(int roleId) {
		return roleDomainMap.get(roleId);
	}

	private void assemblingRole(Role role) {
		int roleId = role.getId();
		RoleDomain roleDomain = getRoleDomain(role.getOccupationId());
		role.setAttributeMap(roleDomain.getAttributeMap());

		List<RoleSkill> roleSkills = RoleDao.INSTANCE.getRoleSkill(roleId);

		// 建立用户技能
		Map<Integer, RoleSkill> roleSkillmap = new HashMap<>();
		for (RoleSkill roleSkill : roleSkills) {
			roleSkillmap.put(roleSkill.getSkillId(), roleSkill);
		}
		role.setSkillMap(roleSkillmap);

		// 建立用户物品栏
		List<BagMaterial> bagMaterials = MaterialDao.INSTANCE.getallUserBagMaterial(roleId);
		List<UserTreasure> userTreasureList = MaterialDao.INSTANCE.getAllTreasure(roleId);
		List<UserEquipment> userEquiment = MaterialDao.INSTANCE.getRoleEquiment(roleId);

		for (BagMaterial bagMaterial : bagMaterials) {
			role.pushMaterial(bagMaterial);
		}

		for (UserEquipment roleEquipment : userEquiment) {
			UserEquipment userEquipment = UserEquimentService.INSTANCE.getUserEquiment(roleEquipment.getUniqueId());
			role.setEquipment(userEquipment);
		}

		for (UserTreasure userTreasure : userTreasureList) {
			UserTreasure treasure = role.getRoleTreasure(userTreasure.getId());
			treasure.changeQuantity(userTreasure.getQuantity());
		}

		// 组建任务信息
		List<RoleMission> missions = MissionDao.INSTANCE.getRoleMissions(roleId);
		for (RoleMission mission : missions) {
			role.reciveMission(mission);
		}

		// 如果队伍没了的话就设置队伍为空
		if (role.getTeamId() != 0 && TeamService.INSTANCE.getTeam(role.getTeamId()) == null) {
			role.setTeamId(0);
		}

		SkillService.INSTANCE.addBuff(role, 1);
		SkillService.INSTANCE.addBuff(role, 2);
	}

	/**
	 * 判断一个角色是否在线
	 * 
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public boolean isUserRoleOnline(int userId, int roleId) {
		if (getUserUsingRole(userId) == null) {
			return false;
		}
		return getUserUsingRole(userId).getId() == roleId;
	}

	/**
	 * 获取用户的其中一个角色
	 * 
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public Role getUserRole(int userId, int roleId) {
		try {
			Role role = cache.get(userId).get(roleId);
			return role;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取用户所有的角色
	 * 
	 * @param userId
	 * @return
	 */
	private Map<Integer, Role> getUserAllRole(int userId) {

		List<Role> roles = RoleDao.INSTANCE.selectUserRole(userId);

		for (Role role : roles) {
			assemblingRole(role);
		}
		Map<Integer, Role> map = roles.stream().collect(Collectors.toMap(Role::getId, a -> a));

		return map;
	}

	/**
	 * 处理用户掉线事件
	 */
	private EventBuilder<UserLostData> userLostEvent = new EventBuilder<UserLostData>() {

		@Override
		public void handler(UserLostData userLostData) {

			int userId = userLostData.getUser().getUserId();
			Role role = userRoleMap.remove(userId);

			if (role == null) {
				return;
			}

			RoleDao.INSTANCE.updateRole(role);

			roleToUser.remove(role.getId());
			MaterialService.INSTANCE.persistenceRoleMatetrial(role);
			MissionDao.INSTANCE.insertMission(role.getRoleMissionMap().values());

			System.out.println("删除用户缓存使用角色");
		}
	};

	// 处理升级的事务
	private EventBuilder<UpdateLevelData> levelUpEvent = new EventBuilder<UpdateLevelData>() {

		@Override
		public void handler(UpdateLevelData updateLevelData) {

			Role role = updateLevelData.getRole();
			if (isOnline(role.getId())) {
				User user = UserService.INSTANCE.getUser(role.getUserId());

				ReplyDomain replyDomain = new ReplyDomain();
				replyDomain.setIntDomain("恭喜你，等级达到了", updateLevelData.getNewLevel());
				RoleExtension.notify(user, replyDomain);
			}
		}
	};

}
