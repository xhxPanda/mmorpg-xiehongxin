package com.hh.mmorpg.server.friend;

import java.sql.SQLException;
import java.util.List;

import com.hh.mmorpg.domain.Friend;
import com.hh.mmorpg.domain.FriendApply;
import com.hh.mmorpg.jdbc.JDBCManager;

public class FriendDao {

	public static final FriendDao INSTANCE = new FriendDao();

	private FriendDao() {
	}

	private static final String ADD_FRIEND = "INSERT INTO `friend` (`roleId`, `friendRoleId`, `time`) VALUES (?, ?, ?)";
	private static final String SELECT_FRIENDS = "SELECT * FROM friend where roleId = ?";
	private static final String DELETE_FRIENDS = "DELETE FROM friend where roleId = ? AND friendId = ?";

	private static final String ADD_APPLICATION = "INSERT INTO `friendapply` (`friendId`, `roleId`, `roleName`, `roleLevel`, `content`) VALUES (?, ?, ?, ?, ?)";
	private static final String SELECT_FRIEND_APPLY = "SELECT * FROM friend where friendId = ?";
	private static final String DELETE_FRIEND_APPLY = "DELETE FROM friendApply where friendId = ? AND roleId = ?";

	public int insertFriend(Friend friend) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(ADD_FRIEND,
				new Object[] { friend.getRoleId(), friend.getFriendId(), friend.getTime() });
	}

	public int insertFriendApply(FriendApply friendApply) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(ADD_APPLICATION,
				new Object[] { friendApply.getApplyRoleId(), friendApply.getRoleId(), friendApply.getRoleName(),
						friendApply.getRoleLevel(), friendApply.getContent() });
	}

	@SuppressWarnings("unchecked")
	public List<Friend> getRoleFriends(int roleId) {
		List<Friend> list = null;
		try {
			list = (List<Friend>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_FRIENDS,
					new Object[] { roleId }, Friend.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int deleteFriend(int roleId, int friendId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_FRIENDS, new Object[] { roleId, friendId });
	}

	@SuppressWarnings("unchecked")
	public List<FriendApply> getRoleFriendsApply(int roleId) {
		List<FriendApply> list = null;
		try {
			list = (List<FriendApply>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(SELECT_FRIEND_APPLY,
					new Object[] { roleId }, FriendApply.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public int deleteFriendApply(int roleId, int appId) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(DELETE_FRIEND_APPLY, new Object[] { roleId, appId });
	}
}