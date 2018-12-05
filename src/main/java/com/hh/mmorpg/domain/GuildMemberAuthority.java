package com.hh.mmorpg.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hh.mmorpg.jdbc.ResultBuilder;

/**
 * 公会各等级的权限，由会长指定，会长拥有最大权力
 * 
 * @author 37
 *
 */
public class GuildMemberAuthority {

	private int guildId;
	private GuildMemberIdentity guildMemberIdentity;
	private boolean canTickMember; // 是否能踢人
	private boolean canSeeBank; // 是否可以浏览银行
	private int takeBankMaterialNum; // 成员每天可以获取多少物品在每一层银行里面
	private boolean canSendPublicMessage; // 是否可以发公会信息
	private int canUseGold; // 每天可以使用的金币

	public GuildMemberAuthority(int guildId, int guildMemberIdentityId, boolean canTickMember, boolean canSeeBank,
			int takeBankMaterialNum, boolean canSendPublicMessage, int canUseGold) {
		this.guildId = guildId;
		this.guildMemberIdentity = GuildMemberIdentity.getGuildMemberIdentity(guildMemberIdentityId);
		this.canTickMember = canTickMember;
		this.canSeeBank = canSeeBank;
		this.takeBankMaterialNum = takeBankMaterialNum;
		this.canSendPublicMessage = canSendPublicMessage;
		this.canUseGold = canUseGold;
	}

	public GuildMemberAuthority(int guildId, GuildMemberIdentity guildMemberIdentity) {
		this.guildId = guildId;
		this.guildMemberIdentity = guildMemberIdentity;

		// 会长拥有所有权限
		if (guildMemberIdentity.getId() == GuildMemberIdentity.PRESIDENT.getId()) {
			this.canTickMember = true;
			this.canSendPublicMessage = true;
			this.canSeeBank = true;
			this.takeBankMaterialNum = -1;
			this.canUseGold = -1;
		} else {
			this.canTickMember = false;
			this.canSendPublicMessage = false;
			this.canSeeBank = false;
			this.takeBankMaterialNum = 0;
			this.canUseGold = 0;
		}
		
		
	}

	public void setCanSeeBank(boolean canSeeBank) {
		this.canSeeBank = canSeeBank;
	}

	public void setTakeBankMaterialNum(int takeBankMaterialNum) {
		this.takeBankMaterialNum = takeBankMaterialNum;
	}

	public void setCanUseGold(int canUseGold) {
		this.canUseGold = canUseGold;
	}

	public int getGuildId() {
		return guildId;
	}

	public boolean isCanSeeBank() {
		return canSeeBank;
	}

	public int getTakeBankMaterialNum() {
		return takeBankMaterialNum;
	}

	public GuildMemberIdentity getGuildMemberIdentity() {
		return guildMemberIdentity;
	}

	public void setGuildMemberIdentity(GuildMemberIdentity guildMemberIdentity) {
		this.guildMemberIdentity = guildMemberIdentity;
	}

	public int getCanUseGold() {
		return canUseGold;
	}

	public boolean isCanTickMember() {
		return canTickMember;
	}

	public void setCanTickMember(boolean canTickMember) {
		this.canTickMember = canTickMember;
	}

	public boolean isCanSendPublicMessage() {
		return canSendPublicMessage;
	}

	public void setCanSendPublicMessage(boolean canSendPublicMessage) {
		this.canSendPublicMessage = canSendPublicMessage;
	}

	public static final ResultBuilder<GuildMemberAuthority> BUILDER = new ResultBuilder<GuildMemberAuthority>() {
		@Override
		public GuildMemberAuthority build(ResultSet result) throws SQLException {
			// TODO Auto-generated method stub
			int guildId = result.getInt("guildId");
			int guildMemberIdentityId = result.getInt("guildMemberIdentityId");
			boolean canTickMember = result.getBoolean("canTickMember");
			boolean canSeeBank = result.getBoolean("canSeeBank");
			int takeBankMaterialNum = result.getInt("takeBankMaterialNum");
			boolean canSendPublicMessage = result.getBoolean("canSendPublicMessage");
			int canUseGold = result.getInt("canUseGold");
			
			return new GuildMemberAuthority(guildId, guildMemberIdentityId, canTickMember, canSeeBank,
					takeBankMaterialNum, canSendPublicMessage, canUseGold);
		}
	};
}
