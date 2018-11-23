package com.hh.mmorpg.server.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.LevelLimitDomain;

public class LevelLimitXmlResolutionManager {

	public static final LevelLimitXmlResolutionManager INSTANCE = new LevelLimitXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, LevelLimitDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\level.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, LevelLimitDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> roles = rootElm.elements("level");
		for (Element element : roles) {
			int level = Integer.parseInt(element.attributeValue("level"));
			int needExp = Integer.parseInt(element.attributeValue("needExp"));
			String resoreHpAndMp = element.attributeValue("resoreHpAndMp");

			LevelLimitDomain levelLimitDomain = new LevelLimitDomain(level, needExp, resoreHpAndMp);
			map.put(levelLimitDomain.getLevel(), levelLimitDomain);
		}

		return map;

	}
}
