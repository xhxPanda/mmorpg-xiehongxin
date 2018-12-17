package com.hh.mmorpg.manager;

import com.hh.mmorpg.CmdHandlerMananger;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.User;

public class CmdRunner implements Runnable {

	private User user;
	private CmdDomain cmdDomain;

	public CmdRunner(User user, CmdDomain cmdDomain) {
		this.user = user;
		this.cmdDomain = cmdDomain;
	}

	@Override
	public void run() {
		CmdHandlerMananger.INSATANCE.invokeHandler(user, cmdDomain);
	}

}
