package com.hh.mmorpg.server.guild;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.GuildLevelDomain;

public class GuildLevelXmlResolutionMenager {
	
	public static final GuildLevelXmlResolutionMenager INSTANCE = new GuildLevelXmlResolutionMenager();
	
	public Map<Integer, GuildLevelDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\guildLevel.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Integer, GuildLevelDomain> map = new HashMap<Integer, GuildLevelDomain>();
		
		Element rootElm = document.getRootElement();
		
		@SuppressWarnings("unchecked")
		List<Element> roles = rootElm.elements("role");
		for (Element element : roles) {
			int level = Integer.parseInt(element.attributeValue("level"));
			int canJoinMemberNum = Integer.parseInt(element.attributeValue("canJoinMemberNum"));
			int needDonatePoint = Integer.parseInt(element.attributeValue("needDonatePoint"));

			GuildLevelDomain domain = new GuildLevelDomain(level, canJoinMemberNum, needDonatePoint);
			
			map.put(domain.getLevel(), domain);
		}
		
		
		return map;
	}

}
