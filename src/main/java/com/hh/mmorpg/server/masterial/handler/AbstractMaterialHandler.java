package com.hh.mmorpg.server.masterial.handler;

import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.result.ReplyDomain;

public abstract class AbstractMaterialHandler {

	public abstract ReplyDomain gainMaterial(Role role, String[] material);

	public abstract ReplyDomain decMasterial(Role role, String[] material);

}
