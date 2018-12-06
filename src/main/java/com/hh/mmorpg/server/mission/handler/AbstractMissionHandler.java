package com.hh.mmorpg.server.mission.handler;

import java.util.List;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.RoleMission;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.event.EventData;
import com.hh.mmorpg.event.EventDealData;
import com.hh.mmorpg.event.EventHandlerManager;
import com.hh.mmorpg.event.EventType;
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
			ReplyDomain domain = new ReplyDomain();
			domain.setStringDomain("cmd", MissionExtension.NOTIFY_MISSION_COMPETE);
			domain.setStringDomain("任务名称", mission.getName());
		}

		// 抛出完成任务事件
		MissionCompeteData missionCompeteData = new MissionCompeteData(mission.getMissionId(), role);
		EventHandlerManager.INSATNCE.methodInvoke(EventType.MISSION_COMPETE,
				new EventDealData<MissionCompeteData>(missionCompeteData));
	}

}
