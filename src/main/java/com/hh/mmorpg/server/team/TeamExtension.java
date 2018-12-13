package com.hh.mmorpg.server.team;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 10)
public class TeamExtension {

	private TeamService service = TeamService.INSTANCE;

	private static final String GET_TEAM_INFO = "getTeamInfo";
	private static final String INVITE_ROLE_ORGANIZE_TEAM = "inviteRoleJoinTeam"; // 邀请人进入队伍
	private static final String DEAL_TEAM_APPLY = "dealTeamApply"; // 处理邀请队伍的请求
	private static final String QUIT_TEAM = "quitTeam"; // 退出队伍
	private static final String TRANSFER_CHAPION = "transferCaptain"; // 转让队长
	private static final String TICK_TEAM_MATE = "tickTeamMate"; // 踢人

	public static final String NOTIFY_TEAM_INVITE = "队伍邀请";
	public static final String NOTIFY_BE_TICKED = "被踢出队伍";
	public static final String NOTIFY_TEAM_MATE_QUIT = "退出队伍";

	@CmdService(cmd = GET_TEAM_INFO)
	public void getTeamInfo(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.getTeamInfo(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = INVITE_ROLE_ORGANIZE_TEAM)
	public void inviteRoleOrganizeTeam(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);
		ReplyDomain replyDomain = service.inviteRoleOrganizeTeam(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DEAL_TEAM_APPLY)
	public void dealTeamApply(User user, CmdDomain cmdDomain) {

		int roleId = cmdDomain.getIntParam(2);
		boolean isAgree = cmdDomain.getBooleanParam(3);

		ReplyDomain replyDomain = service.dealTeamApply(user, roleId, isAgree);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = QUIT_TEAM)
	public void quitTeam(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.quitTeam(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = TRANSFER_CHAPION)
	public void transferCaptain(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);
		ReplyDomain replyDomain = service.transferCaptain(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = TICK_TEAM_MATE)
	public void tickTeamMate(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);
		ReplyDomain replyDomain = service.tickTeamMate(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyRole(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
