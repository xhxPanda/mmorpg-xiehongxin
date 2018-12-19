package com.hh.mmorpg.server.guild;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.NotifiesWarehouse;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * 
 * @author xhx 公会
 */

@Extension
public class GuildExtension {

	private GuildSerivice serivice = GuildSerivice.INSTANCE;

	private static final String SHOW_ALL_GUILD = "showAllGuild";// 展示所有公会
	private static final String SHOW_GUILD_INFO = "showGuildInfo";// 展示公会信息
	private static final String SHOW_GUILD_MEMBER = "showGuildMember"; // 展示公会会员

	/**
	 * 公会内部功能
	 */
	private static final String CREAT_GUILD = "creatGuild"; // 创建公会
	private static final String SEND_GUILD_APPLY = "sendGuildApply"; // 发送公会申请
	private static final String EXAMINATION_APPLY = "eaminationApply"; // 审查申请
	private static final String DONATE_MATERIAL = "donateMaterial"; // 公会捐献物品
	private static final String DONATE_TREASURE = "donateTreasure"; // 公会捐献财富
	private static final String TICK_OUT_MEMBER = "tickOutMember"; // 踢出公会
	private static final String LEAVE_GUILD = "leaveGuild"; // 退出公会
	private static final String TRANSFER_GUILD = "transferGuild"; // 转让公会
	private static final String EXTRACT_MATERIAL = "extractMaterial"; // 提取物品
	private static final String EXTRACT_TREASURE = "extractTreasure"; // 提取财富
	private static final String SHOW_GUILD_BANK = "showGuildBank"; // 展示公会仓库
	private static final String SHOW_GUILD_APPLY = "showGuildApply"; // 展示公会申请

	/**
	 * notify
	 */
	public static final String NOTIFY_USER_JOIN_GUILD = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_USER_JOIN_GUILD");
	public static final String NOTIFY_USER_TICK_OUT = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_USER_TICK_OUT");
	public static final String NOTIFY_PRESIDENT_CHANGE = NotifiesWarehouse.INSTANCE
			.getNotifyContent("NOTIFY_PRESIDENT_CHANGE");

	@CmdService(cmd = SHOW_ALL_GUILD)
	public void showAllGuild(User user, CmdDomain cmdDomain) {

		ReplyDomain replyDomain = serivice.showAllGuild(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SHOW_GUILD_INFO)
	public void showGuildInfo(User user, CmdDomain cmdDomain) {

		ReplyDomain replyDomain = serivice.showGuildInfo(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SHOW_GUILD_MEMBER)
	public void showGuildMember(User user, CmdDomain cmdDomain) {

		ReplyDomain replyDomain = serivice.showGuildMember(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SHOW_GUILD_BANK)
	public void showGuildBank(User user, CmdDomain cmdDomain) {

		ReplyDomain replyDomain = serivice.showGuildBank(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SHOW_GUILD_APPLY)
	public void showGuildApply(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = serivice.showGuildApply(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = CREAT_GUILD)
	public void creatGuild(User user, CmdDomain cmdDomain) {
		String name = cmdDomain.getStringParam(1);
		String declaration = cmdDomain.getStringParam(2);

		ReplyDomain replyDomain = serivice.creatGuild(user, name, declaration);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SEND_GUILD_APPLY)
	public void sendGuildApply(User user, CmdDomain cmdDomain) {
		int guildId = cmdDomain.getIntParam(1);
		String content = cmdDomain.getStringParam(2);

		ReplyDomain replyDomain = serivice.sendGuildApply(user, guildId, content);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = EXAMINATION_APPLY)
	public void eaminationApply(User user, CmdDomain cmdDomain) {
		int applyId = cmdDomain.getIntParam(1);
		boolean isAggre = cmdDomain.getBooleanParam(2);

		ReplyDomain replyDomain = serivice.eaminationApply(user, applyId, isAggre);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = TICK_OUT_MEMBER)
	public void tickOutMember(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(1);

		ReplyDomain replyDomain = serivice.tickOutRole(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DONATE_MATERIAL)
	public void donateMaterial(User user, CmdDomain cmdDomain) {
		int index = cmdDomain.getIntParam(1);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = serivice.donateMaterial(user, index, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DONATE_TREASURE)
	public void donateTreasure(User user, CmdDomain cmdDomain) {
		int id = cmdDomain.getIntParam(1);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = serivice.donateTreasure(user, id, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = EXTRACT_MATERIAL)
	public void extractMaterial(User user, CmdDomain cmdDomain) {
		int index = cmdDomain.getIntParam(1);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = serivice.extractMaterial(user, index, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = EXTRACT_TREASURE)
	public void extractTreasure(User user, CmdDomain cmdDomain) {
		int id = cmdDomain.getIntParam(1);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = serivice.guildReimbursement(user, id, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = LEAVE_GUILD)
	public void leaveGuild(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = serivice.leaveGuild(user);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = TRANSFER_GUILD)
	public void transferGuild(User user, CmdDomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(1);
		ReplyDomain replyDomain = serivice.transferGuild(user, roleId);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
