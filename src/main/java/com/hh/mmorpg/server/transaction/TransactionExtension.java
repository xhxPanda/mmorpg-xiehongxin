package com.hh.mmorpg.server.transaction;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 10)
public class TransactionExtension {

	private TransactionService service = TransactionService.INSTANCE;

	private static final String REQUEST_DELL = "requestTransaction"; // 发起交易请求
	private static final String DEAL_DELL_REQUEST = "dealTransactionRequest"; // 处理交易请求
	private static final String SET_MATERIAL = "setMaterial"; // 放置背包交易物
	private static final String SET_TREASURE = "setTreasure"; // 放置财富交易物
	private static final String CHECK_CONFIRM = "checkConfirm"; // 确认交易
	private static final String STOP_TRANSACTION = "stopTransaction"; // 取消交易

	public static final String NOTIFY_ROLE_REQUEST_TRANSACTION = "通知有人请求交易";
	public static final String NOTIFY_ROLE_REJECT_TRANSACTION = "拒绝交易";
	public static final String NOTIFY_TRANSACTION_START = "开始交易";
	public static final String NOTIFY_TRANSACTION_SHUTDOWN = "对方中断交易";

	@CmdService(cmd = REQUEST_DELL)
	public void requestTransaction(User user, CMDdomain cmdDomain) {
		int roleId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.requestTransaction(user, roleId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DEAL_DELL_REQUEST)
	public void dealTransactionRequest(User user, CMDdomain cmdDomain) {
		boolean isAccept = cmdDomain.getBooleanParam(3);
		int roleId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.dealDellRequest(user, roleId, isAccept);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SET_MATERIAL)
	public void setMaterial(User user, CMDdomain cmdDomain) {
		int index = cmdDomain.getIntParam(2);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.setMaterial(user, index, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SET_TREASURE)
	public void setTreasure(User user, CMDdomain cmdDomain) {
		int id = cmdDomain.getIntParam(2);
		int num = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.setTreasure(user, id, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = CHECK_CONFIRM)
	public void checkConfirm(User user, CMDdomain cmdDomain) {
		ReplyDomain replyDomain = service.checkConfirm(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = STOP_TRANSACTION)
	public void stopTransaction(User user, CMDdomain cmdDomain) {

	}

	public static void notifyRole(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
