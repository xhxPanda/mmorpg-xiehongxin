package com.hh.mmorpg.server.masterial;

import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 5)
public class MasterialExtension {

	private static final String NOTIFY_USER_GAIN_MATERIAL = "5_100";
	private static final String NOTIFY_USER_DEC_MATERIAL = "5_101";

	public static void notifyMaterialGain(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_GAIN_MATERIAL);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	public static void notifyMaterialDec(User user, ReplyDomain replyDomain) {
		replyDomain.setStringDomain("cmd", NOTIFY_USER_DEC_MATERIAL);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
