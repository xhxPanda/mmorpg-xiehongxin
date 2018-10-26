package com.hh.mmorpg.server.scene.xmlResolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Attribute;
import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.NpcRole;
import com.hh.mmorpg.domain.RoleSkill;
import com.hh.mmorpg.domain.Scene;

public class SenceXMLResolution {

	public static final SenceXMLResolution INSTANCE = new SenceXMLResolution();

	@SuppressWarnings("unchecked")
	public Map<Integer, Scene> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\scene.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, Scene> map = new HashMap<>();

		Element rootElm = document.getRootElement();
		List<Element> sences = rootElm.elements("scene");
		for (Element element : sences) {
			String name = element.attributeValue("name");
			int id = Integer.parseInt(element.attributeValue("id"));
			String neighborScenes = element.attributeValue("neighborScenes");
			boolean isCanBattle = Boolean.parseBoolean(element.attributeValue("isCanBattle"));
			
			Scene scene = new Scene(id, name, neighborScenes, isCanBattle);
			map.put(scene.getId(), scene);

			// 生成npc
			Map<Integer, NpcRole> npcMap = new HashMap<>();
			Element npcEle = element.element("npcs");
			List<Element> npcs = npcEle.elements("npc");
			for (Element npc : npcs) {
				String npcName = npc.attributeValue("name");
				int npcId = Integer.parseInt(npc.attributeValue("id"));

				NpcRole npcRole = new NpcRole(npcId, npcName);
				npcMap.put(npcRole.getId(), npcRole);
			}

			// 生成怪物
			Map<Integer, Monster> monsterMap = new HashMap<>();
			Element monsterEle = element.element("monsters");
			List<Element> monsters = monsterEle.elements("monster");
			for (Element monsterchlEle : monsters) {
				String monsterName = monsterchlEle.attributeValue("name");
				int monsterId = Integer.parseInt(monsterchlEle.attributeValue("id"));
				int freshTime = Integer.parseInt(monsterchlEle.attributeValue("freshTime"));
				int count = Integer.parseInt(monsterchlEle.attributeValue("count"));
				String attributeStr = monsterchlEle.attributeValue("attribute");
				String skillsStr = monsterchlEle.attributeValue("skills");

				for (int i = 0; i < count; i++) {
					int uniqueId = IncrementManager.INSTANCE.increase("monster");
					Monster monster = new Monster(monsterId, uniqueId, monsterName, freshTime, id);

					Map<Integer, Attribute> attributeMap = new HashMap<>();
					for (String strList : attributeStr.split(",")) {
						String str[] = strList.split(":");
						Attribute attribute = new Attribute(Integer.parseInt(str[0]), Integer.parseInt(str[2]), str[1]);
						attributeMap.put(attribute.getId(), attribute);
					}

					Map<Integer, RoleSkill> roleSkillMap = new HashMap<>();
					for (String strList : skillsStr.split(",")) {
						int skillId = Integer.parseInt(strList);
						roleSkillMap.put(skillId, new RoleSkill(skillId));
					}
					monster.setAttributeMap(attributeMap);
					monster.setSkillMap(roleSkillMap);
					monsterMap.put(monster.getUniqueId(), monster);

					// 解析掉落物品的概率
					Map<String, Integer> killFallItemMap = new HashMap<>();
					Element killAndFallEle = element.element("killAndFall");

					String fallStr = killAndFallEle.attributeValue("killAndFall");
					String fallStrList[] = fallStr.split(",");

					for (String s : fallStrList) {
						String item = s.split("$")[0];
						int possibility = Integer.parseInt(s.split("$")[1]);
						killFallItemMap.put(item, possibility);
					}
					monster.setKillFallItemMap(killFallItemMap);
				}
			}
			scene.setMonsterMap(monsterMap);
			scene.setNpcRoleMap(npcMap);
		}
		return map;
	}

}
