package com.hh.mmorpg.server.masterial;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 5)
public class MaterialExtension {

	private static final String SHOW_GOODS = "showGoods";
	private static final String BUY_GOODS = "buyGoods";
	private static final String SELL_GOODS = "sellGoods";

	private static final String USE_MATERIAL = "useMaterial";

	private static final String SHOW_ALL_MATERIAL = "showAllMaterial";
	
	private static final String ARRANGE_BAG = "arrangeBag";
	
	private static final String SORT_BAG = "sortBag";

	private static final String NOTIFY_USER_GAIN_MATERIAL = "物品新增";
	private static final String NOTIFY_USER_DEC_MATERIAL = "物品减少";

	@CmdService(cmd = SHOW_GOODS)
	public void showGoods(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.showGoods(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = BUY_GOODS)
	public void buyGoods(User user, CMDdomain cmDdomain) {
		int goodsId = cmDdomain.getIntParam(2);
		int num = cmDdomain.getIntParam(3);

		ReplyDomain replyDomain = MaterialService.INSTANCE.buyGoods(user, goodsId, num);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SELL_GOODS)
	public void sellGoods(User user, CMDdomain cmDdomain) {
		String materialStr = cmDdomain.getStringParam(2);

		ReplyDomain replyDomain = MaterialService.INSTANCE.sellGoods(user, materialStr);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SHOW_ALL_MATERIAL)
	public void showAllMaterial(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.showAllMaterial(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = USE_MATERIAL)
	public void useMaterial(User user, CMDdomain cmDdomain) {

		int index = cmDdomain.getIntParam(2);

		ReplyDomain replyDomain = MaterialService.INSTANCE.useMaterial(user, index);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = ARRANGE_BAG)
	public void arrangeBag(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.arrangeBag(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = SORT_BAG)
	public void sortBag(User user, CMDdomain cmDdomain) {
		int fromIndex = cmDdomain.getIntParam(2);
		int toIndex = cmDdomain.getIntParam(3);
		ReplyDomain replyDomain = MaterialService.INSTANCE.sortBag(user, fromIndex, toIndex);
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
