package com.hh.mmorpg.server.masterial.handler.xmlManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.EquimentDomain;

public class EquimentXmlResolutionManager {

	public static final EquimentXmlResolutionManager INSTANCE = new EquimentXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, EquimentDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\equiment.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Map<Integer, EquimentDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> equipments = rootElm.elements("equipment");
		for (Element element : equipments) {
			int id = Integer.parseInt(element.attributeValue("id"));
			int equimentType = Integer.parseInt(element.attributeValue("equimentType"));
			String name = element.attributeValue("name");
			int maxDurability = Integer.parseInt(element.attributeValue("maxDurability"));

			String attribute = element.attributeValue("attribute");
			String sellPrice = element.attributeValue("sellPrice");

			int equimentSource = Integer.parseInt(element.attributeValue("equimentSource"));
			int equimentLevel = Integer.parseInt(element.attributeValue("equimentLevel"));

			EquimentDomain equimentDomain = new EquimentDomain(id, equimentType, attribute, name, maxDurability,
					sellPrice, equimentSource, equimentLevel);
			map.put(id, equimentDomain);
		}

		return map;
	}

	public static final void main(String args[]) {
		EquimentXmlResolutionManager.INSTANCE.resolution();
	}

}
