package com.hh.mmorpg.server.masterial;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 5)
public class MaterialExtension {

	private static final String BUY_GOODS = "5_1";
	private static final String SELL_GOODS = "5_2";

	private static final String SHOW_ALL_MATERIAL = "5_3";

	private static final String NOTIFY_USER_GAIN_MATERIAL = "5_100";
	private static final String NOTIFY_USER_DEC_MATERIAL = "5_101";

	@CmdService(cmd = BUY_GOODS)
	public void buyGoods(User user, CMDdomain cmDdomain) {
		int goodsId = cmDdomain.getIntParam("gi");
		int num = cmDdomain.getIntParam("n");

		ReplyDomain replyDomain = MaterialService.INSTANCE.buyGoods(user, goodsId, num);
		replyDomain.setStringDomain("cmd", BUY_GOODS);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SELL_GOODS)
	public void sellGoods(User user, CMDdomain cmDdomain) {
		String materialStr = cmDdomain.getStringParam("ms");

		ReplyDomain replyDomain = MaterialService.INSTANCE.sellGoods(user, materialStr);
		replyDomain.setStringDomain("cmd", SELL_GOODS);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SHOW_ALL_MATERIAL)
	public void showAllMaterial(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.showAllMaterial(user);
		replyDomain.setStringDomain("cmd", SHOW_ALL_MATERIAL);
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
