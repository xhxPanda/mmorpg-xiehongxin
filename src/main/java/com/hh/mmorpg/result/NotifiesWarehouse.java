package com.hh.mmorpg.result;

import java.util.Map;

/**
 * 一些需要发到前端的文字由配置文件统一管理，在这个类中统一取出
 * 
 * @author 37
 *
 */
public class NotifiesWarehouse {

	public static final NotifiesWarehouse INSTANCE = new NotifiesWarehouse();

	private Map<String, String> notifyMap;

	private NotifiesWarehouse() {
		notifyMap = NotifyXmlResolutionManager.INSTANCE.resolution();
	}

	/**
	 * 获取notify的文本
	 * 
	 * @param name
	 * @return
	 */
	public String getNotifyContent(String name) {
		return notifyMap.get(name);
	}
}
