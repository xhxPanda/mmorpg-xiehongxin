package com.hh.mmorpg.server.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Email;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.masterial.MaterialService;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.service.user.UserService;

public class EmailService {

	public static final EmailService INSTANCE = new EmailService();

	// 热点数据，用户角色数据缓存
	private LoadingCache<Integer, Map<Integer, Email>> cache = CacheBuilder.newBuilder()
			.refreshAfterWrite(10, TimeUnit.MINUTES).expireAfterAccess(10, TimeUnit.MINUTES).maximumSize(1000)
			.build(new CacheLoader<Integer, Map<Integer, Email>>() {
				@Override
				/** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
				public Map<Integer, Email> load(Integer appKey) {
					return getRoleAllEmailFromDB(appKey);
				}

			});

	public ReplyDomain sendEmail(User user, int recipientRoleId, int recipientId, String bonusStr, String content) {
		int userId = user.getUserId();

		Role role = RoleService.INSTANCE.getUserRole(recipientId, recipientRoleId);
		if (role == null) {
			return ReplyDomain.FAILE;
		}

		Role senderRole = RoleService.INSTANCE.getUserUsingRole(userId);

		int id = IncrementManager.INSTANCE.increase("email");
		Email email = new Email(role.getId(), id, content, bonusStr, false, senderRole.getId(), senderRole.getName());

		sendEmail(email, recipientRoleId, recipientId);

		return ReplyDomain.SUCCESS;
	}

	public Map<Integer, Email> getRoleEmail(int roleId) {
		Map<Integer, Email> emailMap = new HashMap<>();
		try {
			emailMap = cache.get(roleId);
		} catch (ExecutionException e) {
			
			e.printStackTrace();
		}
		return emailMap;
	}

	public ReplyDomain readEmail(User user, int emailId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		Map<Integer, Email> emailMap = getRoleEmail(role.getId());
		if (emailMap == null || emailMap.size() == 0) {
			return ReplyDomain.SUCCESS;
		}
		Email email = emailMap.get(emailId);
		if (email.isRead())
			return ReplyDomain.SUCCESS;

		if (!email.isRead() && !email.getBonus().isEmpty())
			return ReplyDomain.SUCCESS;

		email.setRead(true);
		EmailDao.INSTANCE.updateEmail(email);
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain getEmailBonus(User user, int emailId) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		Map<Integer, Email> emailMap = getRoleEmail(role.getId());
		if (emailMap == null || emailMap.size() == 0) {
			return ReplyDomain.FAILE;
		}

		Email email = emailMap.get(emailId);
		if (email.isRead())
			return ReplyDomain.SUCCESS;

		if (email.getBonus().isEmpty())
			return ReplyDomain.FAILE;

		MaterialService.INSTANCE.gainMasteral(user, role, email.getBonus());
		return ReplyDomain.SUCCESS;
	}

	private ReplyDomain sendEmail(Email email, int recipientRoleId, int recipientId) {
		EmailDao.INSTANCE.sendEmail(email);
		notifyUserGetEmail(email, recipientRoleId, recipientId);
		return ReplyDomain.SUCCESS;
	}

	private void notifyUserGetEmail(Email email, int recipientRoleId, int recipientId) {
		User user = UserService.INSTANCE.getUser(recipientId);
		if (user == null) {
			return;
		}

		if (RoleService.INSTANCE.isUserRoleOnline(recipientId, recipientRoleId)) {
			// 加入缓存中
			getRoleEmail(recipientRoleId).put(email.getId(), email);
			// notify
			EmailExtension.notifyUserGetEmail(user, email);
		}
	}

	private Map<Integer, Email> getRoleAllEmailFromDB(Integer roleId) {

		List<Email> roles = EmailDao.INSTANCE.getRoleEmail(roleId);
		Map<Integer, Email> map = roles.stream().collect(Collectors.toMap(Email::getId, a -> a));

		return map;
	}

}
