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
	
	private static final String SHOW_EQUIMENT = "showEquiment";
	private static final String TAKE_OFF_EQUIMENT = "takeOffEquiment";
	
	@CmdService(cmd = SHOW_EQUIMENT)
	public void showEquiment(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = service.showEquiment(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = TAKE_OFF_EQUIMENT)
	public void takeOffEquiment(User user, CMDdomain cmDdomain) {
		int equimentType = cmDdomain.getIntParam(2);
		
		ReplyDomain replyDomain = service.takeOffEquiment(user, equimentType);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
}
