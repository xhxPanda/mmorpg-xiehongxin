package com.hh.mmorpg.server.mission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.MissionDomain;

public class MissionXmlResolution {

	public static final MissionXmlResolution INSTANCE = new MissionXmlResolution();

	@SuppressWarnings("unchecked")
	public Map<Integer, MissionDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\mission.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		Map<Integer, MissionDomain> map = new HashMap<>();

		Element rootElm = document.getRootElement();
		List<Element> roles = rootElm.elements("mission");

		for (Element element : roles) {
			int id = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			String bonus = element.attributeValue("bonus");
			String dec = element.attributeValue("dec");
			String missionCondition = element.attributeValue("missionCondition");
			int type = Integer.parseInt(element.attributeValue("type"));
			int needLevel = Integer.parseInt(element.attributeValue("needLevel"));
			boolean isAchievement = Boolean.parseBoolean(element.attributeValue("isAchievement"));
			
			MissionDomain missionDomain = new MissionDomain(id, name, bonus, dec, type, needLevel, missionCondition,
					isAchievement);
			map.put(missionDomain.getId(), missionDomain);
		}

		return map;

	}

//	public static final void main(String args[]) {
//		MissionXmlResolution.INSTANCE.resolution();
//	}

}
