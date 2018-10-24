package com.hh.mmorpg.server.masterial;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 5)
public class MasterialExtension {

	private static final String BUY_GOODS = "5_1";
	private static final String SELL_GOODS = "5_2";

	private static final String NOTIFY_USER_GAIN_MATERIAL = "5_100";
	private static final String NOTIFY_USER_DEC_MATERIAL = "5_101";

	@CmdService(cmd = BUY_GOODS)
	public void buyGoods(User user, CMDdomain cmDdomain) {
		int goodsId = cmDdomain.getIntParam("gi");
		int num = cmDdomain.getIntParam("n");

		ReplyDomain replyDomain = MasterialService.INSTANCE.buyGoods(user, goodsId, num);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyMaterialGain(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_GAIN_MATERIAL);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyMaterialDec(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_DEC_MATERIAL);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
