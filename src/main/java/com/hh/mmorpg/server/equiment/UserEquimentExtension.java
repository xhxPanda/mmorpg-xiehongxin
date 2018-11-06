package com.hh.mmorpg.server.equiment;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 7)
public class UserEquimentExtension {
	
	private UserEquimentService service = UserEquimentService.INSTANCE;
	
	private static final String SHOW_EQUIMENT = "7_1";
	private static final String WEAR_EQUIMENT = "7_2";
	
	@CmdService(cmd = SHOW_EQUIMENT)
	public void showEquiment(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = service.showEquiment(user);
		replyDomain.setStringDomain("cmd", SHOW_EQUIMENT);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = WEAR_EQUIMENT)
	public void wearEquient(User user, CMDdomain cmDdomain) {
		
		int equientId = cmDdomain.getIntParam("ei");
		
		ReplyDomain replyDomain = service.setUserEquiment(user, equientId);
		replyDomain.setStringDomain("cmd", WEAR_EQUIMENT);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	
}
