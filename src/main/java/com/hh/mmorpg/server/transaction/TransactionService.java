package com.hh.mmorpg.server.transaction;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hh.mmorpg.domain.BagMaterial;
import com.hh.mmorpg.domain.Role;
import com.hh.mmorpg.domain.Scene;
import com.hh.mmorpg.domain.Transaction;
import com.hh.mmorpg.domain.User;
import com.hh.mmorpg.domain.UserTreasure;
import com.hh.mmorpg.domain.UserTreasureType;
import com.hh.mmorpg.event.EventBuilder;
import com.hh.mmorpg.event.EventHandler;
import com.hh.mmorpg.event.data.TransactionData;
import com.hh.mmorpg.event.data.UserLostData;
import com.hh.mmorpg.result.ReplyDomain;
import com.hh.mmorpg.server.role.RoleService;
import com.hh.mmorpg.server.scene.SceneService;
import com.hh.mmorpg.service.user.UserService;

public class TransactionService {

	public static final TransactionService INSTANCE = new TransactionService();

	private ConcurrentHashMap<Integer, Transaction> transactionMap;

	// 邀请列表（就不持久化了）
	private ConcurrentHashMap<Integer, Set<Integer>> applyCache;

	private AtomicInteger transactionId;

	private Lock lock;

	private TransactionService() {
		this.transactionMap = new ConcurrentHashMap<>();
		this.transactionId = new AtomicInteger(0);
		this.lock = new ReentrantLock();

		// 注册事件
		EventHandler.INSTANCE.addHandler(UserLostData.class, userLostEvent);
	}

	/**
	 * 请求交易
	 * 
	 * @param user
	 * @param roleId
	 * @return
	 */
	public ReplyDomain requestTransaction(User user, int roleId) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		Scene scene = SceneService.INSTANCE.getUserScene(user.getUserId());
		if (scene == null) {
			return ReplyDomain.NOT_IN_SCENE;
		}

		// 对方不在线，因此失败
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		// 不在一个场景中不能交易
		Scene otherScene = SceneService.INSTANCE.getUserScene(RoleService.INSTANCE.getUserId(roleId));
		if (otherScene.getId() != scene.getId()) {
			return ReplyDomain.OTHER_NOT_IN_SCENE;
		}

		// 已经发过邀请了
		Set<Integer> applySet = applyCache.get(roleId);
		if (applySet != null && applySet.size() > 0) {
			if (applySet.contains(role.getId())) {
				return ReplyDomain.HAS_SENT_APPLY;
			}
		}

		User otherUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));

		ReplyDomain replyDomain = new ReplyDomain();
		replyDomain.setStringDomain("cmd", TransactionExtension.NOTIFY_ROLE_REQUEST_TRANSACTION);
		replyDomain.setIntDomain("对方id", role.getId());
		replyDomain.setStringDomain("对方名称", role.getName());

		TransactionExtension.notifyRole(otherUser, replyDomain);

		addApply(role.getId(), roleId);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 添加交易申请缓存
	 * 
	 * @param roleId
	 * @param beRoleId
	 */
	private void addApply(int roleId, int beRoleId) {

		Set<Integer> set = applyCache.get(beRoleId);
		if (set == null) {
			set = new HashSet<>();
			applyCache.put(beRoleId, set);
		}
		set.add(roleId);
	}

	/**
	 * 处理交易请求，如果同意就可以上锁进行设定双方的交易序号，只需要保证这一步是线程安全的就可以了 以这样的方式缩小锁的粒度
	 * 
	 * @param user
	 * @param roleId
	 * @param isAceept
	 * @return
	 */
	public ReplyDomain dealDellRequest(User user, int roleId, boolean isAceept) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		removeTeamInvition(role.getId(), roleId);

		// 对方不在线
		if (!RoleService.INSTANCE.isOnline(roleId)) {
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		User otherUser = UserService.INSTANCE.getUser(RoleService.INSTANCE.getUserId(roleId));
		if (!isAceept) {
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", TransactionExtension.NOTIFY_ROLE_REJECT_TRANSACTION);
			replyDomain.setStringDomain("对方名称", role.getName());
			TransactionExtension.notifyRole(otherUser, replyDomain);

			return ReplyDomain.SUCCESS;
		}

		lock.lock();
		try {

			Scene scene = SceneService.INSTANCE.getUserScene(user.getUserId());
			if (scene == null) {
				return ReplyDomain.NOT_IN_SCENE;
			}

			// 对方不在线，因此失败
			if (!RoleService.INSTANCE.isOnline(roleId)) {
				return ReplyDomain.OTHER_NOT_ONLINE;
			}

			// 不在一个场景中不能交易
			Scene otherScene = SceneService.INSTANCE.getUserScene(RoleService.INSTANCE.getUserId(roleId));
			if (otherScene.getId() != scene.getId()) {
				return ReplyDomain.OTHER_NOT_IN_SCENE;
			}

			Role otherRole = RoleService.INSTANCE.getUserRole(otherUser.getUserId(), roleId);
			if (otherRole.getTransactionPerson() != 0) {
				return ReplyDomain.IN_TRANSACTION;
			}

			// 生成交易
			int id = transactionId.incrementAndGet();
			Transaction transaction = new Transaction(id, role, otherRole);
			transactionMap.put(transaction.getId(), transaction);

			// 设置交易状态
			role.setTransactionPerson(id);

			otherRole.setTransactionPerson(id);

			// 唤醒对方客户端
			ReplyDomain replyDomain = new ReplyDomain();
			replyDomain.setStringDomain("cmd", TransactionExtension.NOTIFY_TRANSACTION_START);
			replyDomain.setStringDomain("对方名称", role.getName());
			TransactionExtension.notifyRole(otherUser, replyDomain);
		} finally {
			lock.unlock();
		}
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 移除申请
	 * 
	 * @param roleId
	 * @param inviteId
	 */
	private void removeTeamInvition(int roleId, int inviteId) {
		Set<Integer> applySet = applyCache.get(roleId);
		if (applySet == null || applySet.size() == 0) {
			return;
		}
		if (applySet.contains(inviteId))
			applySet.remove(inviteId);
	}

	/**
	 * 放入交易物品
	 * 
	 * @param user
	 * @param materialId
	 * @param num
	 * @return
	 */
	public ReplyDomain setMaterial(User user, int index, int num) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Transaction transaction = transactionMap.get(role.getTransactionPerson());
		if (transaction == null) {
			return ReplyDomain.FAILE;
		}

		Role anotherRole = transaction.getAnotherRole(role.getId());

		// 对方不在线，交易关闭
		if (!RoleService.INSTANCE.isOnline(anotherRole.getId())) {
			stopTransaction(role);
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		BagMaterial bagMaterial = role.getBagMaterialIndex(index);
		if (bagMaterial == null || bagMaterial.getQuantity() < num) {
			return ReplyDomain.NOT_ENOUGH;
		}

		transaction.addMaterial(role.getId(), new BagMaterial(bagMaterial, role.getId(), num));

		ReplyDomain notify = new ReplyDomain();
		notify.setStringDomain("对方放入了", bagMaterial.getName() + "*" + num);
		TransactionExtension.notifyRole(UserService.INSTANCE.getUser(anotherRole.getUserId()), notify);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 放入交易金钱
	 * 
	 * @param user
	 * @param id
	 * @param num
	 * @return
	 */
	public ReplyDomain setTreasure(User user, int id, int num) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Transaction transaction = transactionMap.get(role.getTransactionPerson());
		if (transaction == null) {
			return ReplyDomain.FAILE;
		}

		Role anotherRole = transaction.getAnotherRole(role.getId());

		// 对方不在线，交易关闭
		if (!RoleService.INSTANCE.isOnline(anotherRole.getId())) {
			stopTransaction(role);
			return ReplyDomain.OTHER_NOT_ONLINE;
		}

		UserTreasure userTreasure = role.getRoleTreasure(id);
		if (userTreasure.getQuantity() < num) {
			return ReplyDomain.NOT_ENOUGH;
		}

		transaction.addTreasure(role.getId(), id, num);

		ReplyDomain notify = new ReplyDomain();
		notify.setStringDomain("对方放入了", UserTreasureType.getUserTreasureType(id).getName() + "*" + num);
		TransactionExtension.notifyRole(UserService.INSTANCE.getUser(anotherRole.getUserId()), notify);

		return ReplyDomain.SUCCESS;
	}

	/**
	 * 主动发起停止交易
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain stopTransaction(User user) {
		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());
		ReplyDomain replyDomain = stopTransaction(role);

		return replyDomain;
	}

	/**
	 * 一方停止交易，可以是因为掉线，也可以是因为主动退出
	 * 
	 * @param role
	 * @return
	 */
	private ReplyDomain stopTransaction(Role role) {

		Transaction transaction = transactionMap.get(role.getTransactionPerson());
		if (transaction == null) {
			return ReplyDomain.FAILE;
		}
		lock.lock();
		try {
			role.setTransactionPerson(0);

			Role anotherRoleId = transaction.getAnotherRole(role.getId());
			anotherRoleId.setTransactionPerson(0);

			User anOtherUser = UserService.INSTANCE.getUser(anotherRoleId.getUserId());

			// 在线的话发消息
			if (anOtherUser != null) {
				ReplyDomain notify = new ReplyDomain();

				notify.setStringDomain("cmd", TransactionExtension.NOTIFY_TRANSACTION_SHUTDOWN);
				TransactionExtension.notifyRole(anOtherUser, notify);
			}
		} finally {
			lock.unlock();
		}
		return ReplyDomain.SUCCESS;
	}

	/**
	 * 确认交易
	 * 
	 * @param user
	 * @return
	 */
	public ReplyDomain checkConfirm(User user) {

		Role role = RoleService.INSTANCE.getUserUsingRole(user.getUserId());

		Transaction transaction = transactionMap.get(role.getTransactionPerson());
		if (transaction == null) {
			return ReplyDomain.FAILE;
		}

		if (transaction.getConfirmInfo(role.getId()) != null && transaction.getConfirmInfo(role.getId()) == true) {
			return ReplyDomain.HAS_COMFIRM;
		}

		lock.lock();
		try {
			Role anotherRole = transaction.getAnotherRole(role.getId());

			// 对方不在线，交易关闭
			if (!RoleService.INSTANCE.isOnline(anotherRole.getId())) {
				stopTransaction(role);
				return ReplyDomain.OTHER_NOT_ONLINE;
			}

			transaction.confirm(role.getId());

			if (transaction.judgeIsAllAccept()) {
				ReplyDomain replyDomain = transaction.startTrade();

				if (replyDomain.isSuccess()) {
					// 抛出双方交易的事件
					TransactionData oneData = new TransactionData(role, anotherRole);
					TransactionData twoData = new TransactionData(anotherRole, role);

					EventHandler.INSTANCE.invodeMethod(TransactionData.class, oneData);
					EventHandler.INSTANCE.invodeMethod(TransactionData.class, twoData);
				}
				// 交易完成，关闭交易
				transactionMap.remove(role.getTransactionPerson());
				anotherRole.setTransactionPerson(0);
				role.setTransactionPerson(0);

				return replyDomain;
			} else {
				User anOtherUser = UserService.INSTANCE.getUser(anotherRole.getUserId());
				ReplyDomain notify = new ReplyDomain();
				notify.setStringDomain("r", "对方已确认交易内容");
				TransactionExtension.notifyRole(anOtherUser, notify);
				return ReplyDomain.SUCCESS;
			}
		} finally {
			lock.unlock();
		}

	}

	// 用户下线，交易终止
	private EventBuilder<UserLostData> userLostEvent = new EventBuilder<UserLostData>() {

		@Override
		public void handler(UserLostData userLostData) {
			Role role = userLostData.getRole();

			if (role == null) {
				return;
			}

			stopTransaction(role);
		}
	};

}
