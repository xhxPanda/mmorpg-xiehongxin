package com.hh.mmorpg.server.skill.xmlResolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.BuffDomain;
import com.hh.mmorpg.domain.SkillDomain;

public class SkillXmlResolution {

	public static final SkillXmlResolution INSATNCE = new SkillXmlResolution();

	@SuppressWarnings("unchecked")
	public Map<Integer, SkillDomain> skillResolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\skill.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, SkillDomain> map = new HashMap<>();

		Element rootElm = document.getRootElement();
		List<Element> skills = rootElm.elements("skill");

		for (Element skillTopEle : skills) {
			int id = Integer.parseInt(skillTopEle.attributeValue("id"));
			String name = skillTopEle.attributeValue("name");
			int needMp = Integer.parseInt(skillTopEle.attributeValue("needMp"));
			int cd = Integer.parseInt(skillTopEle.attributeValue("cd"));
			Map<Integer, Integer> buffprobabilityMap = new HashMap<>();

			List<Element> buffs = skillTopEle.elements("buff");
			for (Element buff : buffs) {
				int buffId = Integer.parseInt(buff.attributeValue("id"));
				int probability = Integer.parseInt(buff.attributeValue("probability"));
				buffprobabilityMap.put(buffId, probability);
			}
			
			Map<Integer, Integer> effectAttributeMap = new HashMap<>();
			String effectAttribute = skillTopEle.attributeValue("effectAttribute");
			if(!effectAttribute.isEmpty()) {
				 String  effectAttributeList[] = effectAttribute.split(",");
					for (String str : effectAttributeList) {
						effectAttributeMap.put(Integer.parseInt(str.split(":")[0]), Integer.parseInt(str.split(":")[1]));
					}
			}
		

			Map<Integer, Integer> selfEffectAttributeMap = new HashMap<>();
			String selfEffectAttribute = skillTopEle.attributeValue("selfEffectAttribute");
			if(!selfEffectAttribute.isEmpty()) {
				String selfEffectAttributeList[] = selfEffectAttribute.split(",");
				for (String str : selfEffectAttributeList) {
					selfEffectAttributeMap.put(Integer.parseInt(str.split(":")[0]), Integer.parseInt(str.split(":")[1]));
				}
			}

			SkillDomain skillDomain = new SkillDomain(id, name, cd, effectAttributeMap,
					selfEffectAttributeMap, buffprobabilityMap, needMp);
			map.put(skillDomain.getId(), skillDomain);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, BuffDomain> buffResolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\buff.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, BuffDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> buffs = rootElm.elements("buff");

		for (Element buffsTopEle : buffs) {
			int id = Integer.parseInt(buffsTopEle.attributeValue("id"));
			String name = buffsTopEle.attributeValue("name");
			boolean isBuff = Boolean.parseBoolean(buffsTopEle.attributeValue("isBuff"));
			String attribute = buffsTopEle.attributeValue("attribute");
			long lastTime = Long.parseLong(buffsTopEle.attributeValue("lastTime"));
			long heartbeatTime = Long.parseLong(buffsTopEle.attributeValue("heartbeatTime"));
			boolean isCanResore = Boolean.parseBoolean(buffsTopEle.attributeValue("isCanResore"));

			BuffDomain buffDomain = new BuffDomain(id, name, lastTime, heartbeatTime, isBuff, attribute, isCanResore);
			map.put(id, buffDomain);
		}
		return map;
	}
	
//	public static final void main(String args[]) {
//		new SkillXmlResolution().buffResolution();
//	}
}
