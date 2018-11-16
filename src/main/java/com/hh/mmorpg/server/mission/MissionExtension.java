package com.hh.mmorpg.server.mission;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 9)
public class MissionExtension {

	private MissionService service = MissionService.INSTANCE;

	private static final String ACCEPT_MISSION = "acceptMission"; // 接受任务
	private static final String MISSION_COMPETE = "missionCompete"; // 完成任务
	private static final String GIVE_UP_MISSION = "giveUpMission"; // 放弃任务
	private static final String SHOW_MISSION_CAN_ACCEPT = "showMissionCanAccept"; // 显示可接任务
	private static final String SHOW_MISSION_ACCEPTED = "showMissionAccept"; // 显示已接任务

	@CmdService(cmd = SHOW_MISSION_CAN_ACCEPT)
	public void showMissionCanAccept(User user, CMDdomain cmdDomain) {
		
		ReplyDomain replyDomain = service.showMissionCanAccept(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SHOW_MISSION_ACCEPTED)
	public void showMissionAccept(User user, CMDdomain cmdDomain) {
		
		ReplyDomain replyDomain = service.showMissionAccept(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = ACCEPT_MISSION)
	public void acceptMission(User user, CMDdomain cmdDomain) {
		int missionId = cmdDomain.getIntParam(2);
		
		ReplyDomain replyDomain = service.accpetMission(user, missionId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = MISSION_COMPETE)
	public void missionCompete(User user, CMDdomain cmdDomain) {
		int missionId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.missionCompete(user, missionId);
		
		
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GIVE_UP_MISSION)
	public void giveUpMission(User user, CMDdomain cmdDomain) {
		int missionId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.giveUpMission(user, missionId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

}
