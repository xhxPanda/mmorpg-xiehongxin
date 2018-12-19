package com.hh.mmorpg.server.friend;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.Friend;
import com.hh.mmorpg.domain.FriendApply;
import com.hh.mmorpg.jdbc.ConnectionDeal;

public class FriendDao {

	public static final FriendDao INSTANCE = new FriendDao();

	private FriendDao() {
	}

	private static final String ADD_FRIEND = "INSERT INTO `friend` (`roleId`, `friendRoleId`, `time`) VALUES (?, ?, ?)";
	private static final String SELECT_FRIENDS = "SELECT * FROM friend where roleId = ?";
	private static final String DELETE_FRIENDS = "DELETE FROM friend where roleId = ? AND friendId = ?";

	private static final String ADD_APPLICATION = "INSERT INTO `friendapply` (`friendId`, `roleId`, `roleName`, `userId`, `roleLevel`, `content`) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String SELECT_FRIEND_APPLY = "SELECT * FROM friendapply where friendId = ?";
	private static final String DELETE_FRIEND_APPLY = "DELETE FROM friendApply where friendId = ? AND roleId = ?";

	public void insertFriend(Friend friend) {
		ConnectionDeal.INSTANCE.excuteObject(ADD_FRIEND,
				new Object[] { friend.getRoleId(), friend.getFriendId(), friend.getTime() });
	}

	public void insertFriendApply(FriendApply friendApply) {
		ConnectionDeal.INSTANCE.excuteObject(ADD_APPLICATION,
				new Object[] { friendApply.getApplyRoleId(), friendApply.getRoleId(), friendApply.getRoleName(),
						friendApply.getUserId(), friendApply.getRoleLevel(), friendApply.getContent() });
	}

	public List<Friend> getRoleFriends(int roleId) {
		List<Friend> list = null;
		try {
			list = (List<Friend>) ConnectionDeal.INSTANCE.excuteObjectList(SELECT_FRIENDS,
					new Object[] { roleId }, Friend.BUILDER);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return list;
	}

	public void deleteFriend(int roleId, int friendId) {
		ConnectionDeal.INSTANCE.excuteObject(DELETE_FRIENDS, new Object[] { roleId, friendId });
	}

	public List<FriendApply> getRoleFriendsApply(int roleId) {
		List<FriendApply> list = null;
		try {
			list = (List<FriendApply>) ConnectionDeal.INSTANCE.excuteObjectList(SELECT_FRIEND_APPLY,
					new Object[] { roleId }, FriendApply.BUILDER);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return list;
	}

	public void deleteFriendApply(int roleId, int appId) {
		ConnectionDeal.INSTANCE.excuteObject(DELETE_FRIEND_APPLY, new Object[] { roleId, appId });
	}
}
