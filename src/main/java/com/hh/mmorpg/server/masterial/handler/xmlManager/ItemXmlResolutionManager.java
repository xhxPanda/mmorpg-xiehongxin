package com.hh.mmorpg.server.masterial.handler.xmlManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.ItemDomain;

public class ItemXmlResolutionManager {

	public static final ItemXmlResolutionManager INSTANCE = new ItemXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, ItemDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\item.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, ItemDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> items = rootElm.elements("item");
		for (Element element : items) {
			int id = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			long cd = Long.parseLong(element.attributeValue("cd"));
			String effect = element.attributeValue("effect");
			String buffs = element.attributeValue("buffs");
			String sellPrice = element.attributeValue("sellPrice");
			int pileNum = Integer.parseInt(element.attributeValue("pileNum"));
			map.put(id, new ItemDomain(id, name, effect, buffs, cd, sellPrice, pileNum));
		}
		return map;
	}

	public static final void main(String args[]) {
		ItemXmlResolutionManager.INSTANCE.resolution();
	}

}
