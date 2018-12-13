package com.hh.mmorpg.server.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.domain.RoleDomain;

/**
 * 有关角色信息的xml解析类
 * 
 * @author 37
 *
 */

public class RoleXmlResolutionManager {

	public static final RoleXmlResolutionManager INSTANCE = new RoleXmlResolutionManager();

	@SuppressWarnings("unchecked")
	public Map<Integer, RoleDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\RoleAttribute.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		Map<Integer, RoleDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> roles = rootElm.elements("role");
		for (Element element : roles) {
			int id = Integer.parseInt(element.attributeValue("id"));
			String name = element.attributeValue("name");
			String attributeStr = element.attributeValue("attribute");
			RoleDomain domain = new RoleDomain(id, name, attributeStr);
			map.put(domain.getId(), domain);
		}

		return map;
	}

	public static final void main(String args[]) {
		RoleXmlResolutionManager.INSTANCE.resolution();
	}

}
