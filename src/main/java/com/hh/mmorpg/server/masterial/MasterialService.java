package com.hh.mmorpg.server.masterial;

import java.util.HashMap;
import java.util.Map;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.handler.AbstractMaterialHandler;

public class MasterialService {

	public static final MasterialService INSTANCE = new MasterialService();

	private Map<Integer, AbstractMaterialHandler> handlerMap;

	private MasterialService() {
		this.handlerMap = new HashMap<>();
	}

	public ReplyDomain gainMasteral(User user, Role role, String material) {
		String str[] = material.split(":");

		int type = Integer.parseInt(str[0]);
		if (handlerMap.get(type) == null) {
			return ReplyDomain.FAILE;
		}

		ReplyDomain replyDomain = handlerMap.get(type).gainMaterial(role, str);
		if (replyDomain.isSuccess()) {
			ReplyDomain notify = replyDomain;
			notify.setStringDomain("m", material);
			MasterialExtension.notifyMaterialGain(user, notify);
		}

		return replyDomain;
	}

	public ReplyDomain decMasterial(User user, Role role, String material) {
		String str[] = material.split(":");

		int type = Integer.parseInt(str[0]);
		if (handlerMap.get(type) == null) {
			return ReplyDomain.FAILE;
		}
		ReplyDomain replyDomain = handlerMap.get(type).decMasterial(role, str);
		if (replyDomain.isSuccess()) {
			ReplyDomain notify = replyDomain;
			notify.setStringDomain("m", material);
			MasterialExtension.notifyMaterialDec(user, notify);
		}

		return replyDomain;
	}

}
