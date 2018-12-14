package com.hh.mmorpg.server.masterial;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
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
	@CmdService(cmd = SHOW_GOODS)
	public void showGoods(User user, CmdDomain cmDdomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.showGoods(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = BUY_GOODS)
	public void buyGoods(User user, CmdDomain cmDdomain) {
		int goodsId = cmDdomain.getIntParam(2);
		String numStr = cmDdomain.getStringParam(3);

		// 检验参数
		if (numStr.length() >= 9) {
			ExtensionSender.INSTANCE.sendReply(user, ReplyDomain.FAILE);
		}

		int num = Integer.parseInt(numStr);

		ReplyDomain replyDomain = MaterialService.INSTANCE.buyGoods(user, goodsId, num);

		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SELL_GOODS)
	public void sellGoods(User user, CmdDomain cmDdomain) {
		String materialStr = cmDdomain.getStringParam(2);

		ReplyDomain replyDomain = MaterialService.INSTANCE.sellGoods(user, materialStr);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SHOW_ALL_MATERIAL)
	public void showAllMaterial(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.showAllMaterial(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = USE_MATERIAL)
	public void useMaterial(User user, CmdDomain cmdDomain) {

		int index = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = MaterialService.INSTANCE.useMaterial(user, index);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = ARRANGE_BAG)
	public void arrangeBag(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = MaterialService.INSTANCE.arrangeBag(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = SORT_BAG)
	public void sortBag(User user, CmdDomain cmdDomain) {
		int fromIndex = cmdDomain.getIntParam(2);
		int toIndex = cmdDomain.getIntParam(3);
		ReplyDomain replyDomain = MaterialService.INSTANCE.sortBag(user, fromIndex, toIndex);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyMaterialGain(User user, ReplyDomain replyDomain) {
		// 如果不在线就算了不用通知了
		if (user == null)
			return;
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyMaterialDec(User user, ReplyDomain replyDomain) {
		// 如果不在线就算了不用通知了
				if (user == null)
					return;
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
