package com.hh.mmorpg.server.friend;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.ExtensionSender;

/**
 * 
 * @author xhx 好友系统
 */

@Extension(id = 9)
public class FriendExtension {

	private FriendService service = FriendService.INSTANCE;

	private static final String GET_FRIENDS_INFO = "getFriendsInfo"; // 获取好友列表
	private static final String GET_FRIENDS_APPLICATION = "getFriendsApplication"; // 获取好友申请列表
	private static final String APPLY_ADD_FRIEND = "applyAddFriend"; // 请求添加好友
	private static final String DELETE_FRIEND = "deleteFriend"; // 删除好友
	private static final String DEAL_FRIEND_APPLY = "dealFriendApply";

	public static final String NOTIFY_FRIEND_APPLY = "好友申请";
	public static final String NOTIFY_FRIEND_APPLY_PASS = "好友申请通过";

	@CmdService(cmd = GET_FRIENDS_INFO)
	public void getFriendsInfo(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = service.getRoleFriends(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = GET_FRIENDS_APPLICATION)
	public void getFriendsApplication(User user, CMDdomain cmDdomain) {
		ReplyDomain replyDomain = service.getFriendsApplication(user);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = APPLY_ADD_FRIEND)
	public void applyAddFriend(User user, CMDdomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(2);
		String content = cmDdomain.getStringParam(3);
		ReplyDomain replyDomain = service.applyAddFriend(user, roleId, content);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DEAL_FRIEND_APPLY)
	public void dealFriendApply(User user, CMDdomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(2);
		boolean agree = cmDdomain.getBooleanParam(3);

		ReplyDomain replyDomain = service.dealFriendApply(user, roleId, agree);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	@CmdService(cmd = DELETE_FRIEND)
	public void deleteFriend(User user, CMDdomain cmDdomain) {
		int roleId = cmDdomain.getIntParam(2);

		ReplyDomain replyDomain = service.deleteFriend(user, roleId);
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}

	public static void notifyUser(User user, ReplyDomain replyDomain) {
		ExtensionSender.INSTANCE.sendReply(user, replyDomain);
	}
}
