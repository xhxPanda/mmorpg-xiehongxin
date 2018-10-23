package com.hh.mmorpg.server.email;

import java.util.List;
import java.util.Map;
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
					return getRoleAllEmail(appKey);
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

		sendEmail(email);

		User recipient = UserService.INSTANCE.getUser(recipientId);
		if (recipient != null) {

		}
		return ReplyDomain.SUCCESS;
	}

	public ReplyDomain sendEmail(Email email) {
		EmailDao.INSTANCE.sendEmail(email);

		return ReplyDomain.SUCCESS;
	}

	private Map<Integer, Email> getRoleAllEmail(Integer roleId) {
		// TODO Auto-generated method stub

		List<Email> roles = EmailDao.INSTANCE.getRoleEmail(roleId);
		Map<Integer, Email> map = roles.stream().collect(Collectors.toMap(Email::getId, a -> a));

		return map;
	}

}
