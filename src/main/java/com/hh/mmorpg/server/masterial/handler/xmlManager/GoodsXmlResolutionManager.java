package com.hh.mmorpg.server.masterial.handler.xmlManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.Goods;

public class GoodsXmlResolutionManager {

	public static final GoodsXmlResolutionManager INSTANCE = new GoodsXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, Goods> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\goods.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, Goods> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> goodsEles = rootElm.elements("item");
		for (Element element : goodsEles) {
			int id = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			String item = element.attributeValue("item");
			String price = element.attributeValue("price");

			Goods goodsDomain = new Goods(id, name, item, price);
			map.put(goodsDomain.getId(), goodsDomain);
		}
		return map;
	}
}
