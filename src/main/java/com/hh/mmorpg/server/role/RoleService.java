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
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserEquipment;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.RoleChangeData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.equiment.UserEquimentService;
import com.hh.mmorpg.server.masterial.MaterialDao;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.server.skill.SkillService;

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

		EventHandlerManager.INSATNCE.register(this);
	}

	public ReplyDomain getAllRole(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		Map<Integer, Role> map = getUserAllRole(userId);
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
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
		// TODO Auto-generated method stub
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
		EventHandlerManager.INSATNCE.methodInvoke(EventType.ROLE_CHANGE, new EventDealData<RoleChangeData>(data));

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

	public Integer getUserId(int roleId) {
		return roleToUser.get(roleId);
	}

	public ReplyDomain getUserUsingRole(User user) {
		ReplyDomain replyDomain = new ReplyDomain(ResultCode.SUCCESS);
		Role role = getUserUsingRole(user.getUserId());
		if (role != null) {
			replyDomain.setStringDomain("当前角色", role.toString());
		} else {
			replyDomain.setStringDomain("当前角色", "");
		}
		return replyDomain;
	}

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
		// TODO Auto-generated method stub
		int id = IncrementManager.INSTANCE.increase("role");

		RoleDomain domain = roleDomainMap.get(occupationId);

		Role role = new Role(user.getUserId(), id, name, occupationId, DEFAULT_CAPACITY, 1, 0, 0, 0,
				domain.getAttributeStr(), 0);
		int i = RoleDao.INSTANCE.insertRole(role);
		if (i < 0) {
			return ReplyDomain.FAILE;
		}
		assemblingRole(role);
		getUserAllRole(user.getUserId()).put(role.getId(), role);
		return ReplyDomain.SUCCESS;
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
			// TODO Auto-generated catch block
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

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();

		int userId = userLostData.getUser().getUserId();
		Role role = userRoleMap.remove(userId);

		RoleDao.INSTANCE.updateRole(role);

		roleToUser.remove(role.getId());
		MaterialService.INSTANCE.persistenceRoleMatetrial(role);
		System.out.println("删除用户缓存使用角色");
	}

}
