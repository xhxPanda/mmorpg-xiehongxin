package com.hh.mmorpg.server.skill;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

@Extension
public class SkillExtension {

	private SkillService service = SkillService.INSTANCE;

	private static final String LEANRN_SKILL = "learnSkill";
	private static final String GET_CAN_LEARN_SKILL = "getCanLearnSkill"; // 查看可以学习的技能
	private static final String GET_SKILL = "getSkill"; // 查看已学习的技能

	@CmdService(cmd = GET_CAN_LEARN_SKILL)
	public void getCanLearnSkill(User user, CmdDomain cmdDomain) {

		ReplyDomain replyDomain = service.getCanLearnSkill(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = LEANRN_SKILL)
	public void learnSkill(User user, CmdDomain cmdDomain) {
		int skillId = cmdDomain.getIntParam(1);

		ReplyDomain replyDomain = service.learnSkill(user, skillId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
	
	@CmdService(cmd = GET_SKILL)
	public void getSkill(User user, CmdDomain cmdDomain) {
		ReplyDomain replyDomain = service.getSkill(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

}
