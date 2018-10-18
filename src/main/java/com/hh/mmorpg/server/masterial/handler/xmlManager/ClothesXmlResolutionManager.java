package com.hh.mmorpg.server.masterial.handler.xmlManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.ClothesDomain;

public class ClothesXmlResolutionManager {

	public static final ClothesXmlResolutionManager INSTANCE = new ClothesXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, ClothesDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\clothes.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<Integer, ClothesDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> equipments = rootElm.elements("equipment");
		for (Element element : equipments) {
			int id = Integer.parseInt(element.attributeValue("id"));
			int clothesType = Integer.parseInt(element.attributeValue("clothesType"));
			String name = element.attributeValue("name");
			int maxDurability = Integer.parseInt(element.attributeValue("maxDurability"));

			String attribute = element.attributeValue("attribute");

			ClothesDomain clothesDomain = new ClothesDomain(id, clothesType, attribute, name, maxDurability);
			map.put(id, clothesDomain);
		}

		return map;
	}

}
