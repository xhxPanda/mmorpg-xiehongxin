package com.hh.mmorpg.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class NotifyXmlResolutionManager {

	public static final NotifyXmlResolutionManager INSTANCE = new NotifyXmlResolutionManager();

	public Map<String, String> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\notify.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		Map<String, String> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> notifies = rootElm.elements("notify");
		for (Element element : notifies) {
			String name = element.attributeValue("name");
			String content = element.attributeValue("content");
			map.put(name, content);
		}

		return map;
	}
	
	public static final void main(String args[]) {
		NotifyXmlResolutionManager.INSTANCE.resolution();
	}

}
