package com.hh.mmorpg.server.scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.Event;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.result.ResultCode;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.xmlResolution.SenceXMLResolution;
import com.hh.mmorpg.server.skill.SkillService;

public class SceneService {

	public static final SceneService INSTANCE = new SceneService();

	private Map<Integer, Scene> sceneMap;
	private ConcurrentHashMap<Integer, Integer> sceneUserMap;

	// 错误码
	private static final int CAN_NOT_ENTER = 4;

	private SceneService() {
		sceneUserMap = new ConcurrentHashMap<Integer, Integer>();
		sceneMap = SenceXMLResolution.INSTANCE.resolution();

		EventHandlerManager.INSATNCE.register(this);
	}

	public ReplyDomain userJoinScene(User user, int sceneId) {
		int userId = user.getUserId();

		SceneUserCache sceneUserCache = null;

		Integer oldSceneId = sceneUserMap.get(userId);
		
		if (oldSceneId != null) {
			if(oldSceneId == sceneId) {
				return ReplyDomain.SUCCESS;
			}
			Scene scene = sceneMap.get(oldSceneId);
			if (!scene.isCanEnter(sceneId)) {
				return new ReplyDomain(CAN_NOT_ENTER);
			}
			sceneUserCache = scene.userLeaveScene(user);
		} else {
			// 初始进入场景
			Role role = RoleService.INSTANCE.getUserUsingRole(userId);
			sceneUserCache = new SceneUserCache(userId, role);
		}

		// 进入新场景
		Scene newScene = sceneMap.get(sceneId);

		if (newScene.userEnterScene(sceneUserCache).isSuccess()) {
			sceneUserMap.put(userId, sceneId);
		}

		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain getSeceneUser(User user) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();

		ReplyDomain domain = new ReplyDomain(ResultCode.SUCCESS);

		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}

		Scene scene = sceneMap.get(sceneId);

		domain.setIntDomain("id", sceneId);
		domain.setStringDomain("n", scene.getName());
		domain.setListDomain("u", scene.getUserMap().values());
		domain.setListDomain("npc", scene.getNpcRoleMap().values());
		domain.setListDomain("m", scene.getMonsterMap().values());

		return domain;
	}

	public ReplyDomain attackMonster(User user, int skillId, int monsterId) {
		// TODO Auto-generated method stub
		int userId = user.getUserId();
		Integer sceneId = sceneUserMap.get(userId);
		if (sceneId == null) {
			return ReplyDomain.FAILE;
		}
		
		Scene scene = sceneMap.get(sceneId);
		if (scene == null) {
			return ReplyDomain.FAILE;
		}
		
		Monster monster = scene.getMonster(monsterId);
		if (monster == null) {
			return ReplyDomain.FAILE;
		}
		
		if(monster.isDead()) {
			return ReplyDomain.HAS_DEAD;
		}
		
		Role role = scene.getUserRole(userId);
		RoleSkill roleSkill = role.getRoleSkill(skillId);
		if(roleSkill == null) {
			return ReplyDomain.FAILE;
		}
		long now = System.currentTimeMillis();
		ReplyDomain replyDomain = SkillService.INSTANCE.dealSkillEffect(roleSkill, role, monster, now);
		if(replyDomain.isSuccess()) {
			roleSkill.setLastUseTime(now);
		}
		
		ReplyDomain notifyReplyDomain = new ReplyDomain();
		notifyReplyDomain.setStringDomain("m", monster.toString());
		notifyReplyDomain.setStringDomain("cmd", SceneExtension.NOTIFY_MONSTER_BE_ATTACK);
		scene.notifyAllUser(notifyReplyDomain);
		ReplyDomain domain = new ReplyDomain(ResultCode.SUCCESS);
		return domain;
	}
		
	// 获取用户所在的场景
	public Scene getUserScene(int userId) {
		return sceneMap.get(userId);
	}

	public Map<Integer, Scene> getSceneMap() {
		return sceneMap;
	}

	// 用户下线，把他的缓存删除
	@Event(eventType = EventType.USER_LOST)
	public void handleUserLost(EventDealData<UserLostData> data) {
		UserLostData userLostData = data.getData();
		Integer sceneId = sceneUserMap.remove(userLostData.getUser().getUserId());
		if (sceneId == null) {
			return;
		}
		Scene scene = sceneMap.get(sceneId);

		scene.userLeaveScene(userLostData.getUser());

		System.out.println("用户下线了");
	}

}