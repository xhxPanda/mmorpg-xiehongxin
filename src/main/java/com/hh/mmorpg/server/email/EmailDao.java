package com.hh.mmorpg.server.email;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hh.mmorpg.domain.Email;
import com.hh.mmorpg.jdbc.JDBCManager;

public class EmailDao {
	public static final EmailDao INSTANCE = new EmailDao();

	private static final String SEND_EMAIL = "INSERT INTO `useremali0` (`roleId`, `emailId`, `content`, `bonus`, `read`, `senderId`, `senderName`) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String GET_ROLE_EMAIL = "SELET * FROM `useremali0` WHERE `roleId` = ?";

	public int sendEmail(Email email) {
		return JDBCManager.INSTANCE.getConn("part0").excuteObject(SEND_EMAIL,
				new Object[] { email.getRoleId(), email.getId(), email.getContent(), email.getBonus(), email.isRead(),
						email.getSenderRoleId(), email.getSenderRoleName() });
	}

	@SuppressWarnings("unchecked")
	public List<Email> getRoleEmail(int roleId) {
		try {
			return (List<Email>) JDBCManager.INSTANCE.getConn("part0").excuteObjectList(GET_ROLE_EMAIL,
					new Object[] { roleId }, Email.BUILDER);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<Email>();
	}
}
