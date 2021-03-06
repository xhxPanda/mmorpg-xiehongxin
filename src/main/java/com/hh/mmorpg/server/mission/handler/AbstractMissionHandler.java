package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventData;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.MissionCompeteData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.mission.MissionExtension;
import com.hh.mmorpg.service.user.UserService;

/**
 * 任务处理基类
 * 
 * @author xhx
 *
 */
public abstract class AbstractMissionHandler<T extends EventData> {
	public abstract void dealMission(T eventData, List<RoleMission> missions);

	/**
	 * 完成任务后要做的的东西
	 * 
	 * @param role
	 * @param mission
	 */
	public void dealFinishMission(Role role, RoleMission mission) {
		User user = UserService.INSTANCE.getUser(role.getUserId());

		// 唤醒前端
		if (user != null) {
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", MissionExtension.NOTIFY_MISSION_COMPETE);
			replyDomain.setStringDomain("任务名称", mission.getName());
			MissionExtension.notifyRoleMissionInfo(user, replyDomain);
		}

		// 设置完成的状态
		mission.setStatus(1);

		// 抛出完成任务事件
		MissionCompeteData missionCompeteData = new MissionCompeteData(mission.getMissionId(), role);
		EventHandler.INSTANCE.invodeMethod(MissionCompeteData.class, missionCompeteData);
	}
	
	public void notifyMissionStatusChange(Role role, RoleMission mission) {
		User user = UserService.INSTANCE.getUser(role.getUserId());
		
		if (user != null) {
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", MissionExtension.NOTIFY_MISSION_COMPETE);
			replyDomain.setStringDomain("任务更新", mission.toString());
			MissionExtension.notifyRoleMissionInfo(user, replyDomain);
		}
	}

}
