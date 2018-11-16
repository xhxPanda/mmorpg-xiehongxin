package com.hh.mmorpg.server.guild;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;
import com.hh.mmorpg.server.scene.SceneExtension;

/**
 * 
 * @author xhx 
 *   公会
 */

@Extension(id = 8)
public class GuildExtension {
	
	private GuildSerivice serivice = GuildSerivice.INSTANCE;

	private static final String SHOW_GUILD_INFO = "showGuildInfo";// 展示公会信息
	private static final String CREAT_GUILD = "creatGuild"; // 创建公会
	private static final String SEND_GUILD_APPLY = "sendGuildApply"; // 发送公会申请
	private static final String EXAMINATION_APPLY = "eaminationApply"; // 审查申请
	private static final String DONATE_MATERIAL = "donateMaterial"; // 公会捐献
	private static final String TICK_OUT_MEMBER = "tickOutMember"; // 踢出公会
	private static final String LEAVE_GUILD = "leaveGuild"; // 退出公会
	private static final String TRANSFER_GUILD = "transferGuild"; // 转让公会
	
	public static final String NOTIFY_USER_JOIN_GUILD = "公会申请通过";
	public static final String NOTIFY_USER_TICK_OUT = "踢出公会";
	public static final String NOTIFY_PRESIDENT_CHANGE = "会长变更";
	
	
	@CmdService(cmd = CREAT_GUILD)
	public void creatGuild(User user, CMDdomain cmdDomain) {
		String name = cmdDomain.getStringParam(2);
		String declaration = cmdDomain.getStringParam(3);

		ReplyDomain replyDomain = serivice.creatGuild(user, name, declaration);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SEND_GUILD_APPLY)
	public void sendGuildApply(User user, CMDdomain cmdDomain) {
		int guildId = cmdDomain.getIntParam(2);
		String content = cmdDomain.getStringParam(3);

		ReplyDomain replyDomain = serivice.sendGuildApply(user, guildId, content);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = EXAMINATION_APPLY)
	public void eaminationApply(User user, CMDdomain cmdDomain) {
		int applyId = cmdDomain.getIntParam(2);
		boolean isAggre = cmdDomain.getBooleanParam(3);

		ReplyDomain replyDomain = serivice.eaminationApply(user, applyId, isAggre);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = TICK_OUT_MEMBER)
	public void tickOutMember(User user, CMDdomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = serivice.tickOutRole(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = LEAVE_GUILD)
	public void leaveGuild(User user, CMDdomain cmdDomain) {
		ReplyDomain replyDomain = serivice.leaveGuild(user);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	
	public static void notifyUser(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
