package com.hh.mmorpg.server.skill;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension(id = 11)
public class SkillExtension {

	private SkillService service = SkillService.INSTANCE;

	public static final String LEANRN_SKILL = "learnSkill";

	@CmdService(cmd = LEANRN_SKILL)
	public void learnSkill(User user, CMDdomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(2);

		ReplyDomain replyDomain = service.learnSkill(user, skillId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

}
