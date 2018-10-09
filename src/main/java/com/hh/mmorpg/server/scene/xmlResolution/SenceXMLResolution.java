package com.hh.mmorpg.server.scene.xmlResolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.NpcRole;
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

			Scene scene = new Scene(id, name, neighborScenes);
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
			for (Element npc : monsters) {
				String monsterName = npc.attributeValue("name");
				int monsterId = Integer.parseInt(npc.attributeValue("id"));
				int freshTime = Integer.parseInt(npc.attributeValue("freshTime"));
				int count = Integer.parseInt(npc.attributeValue("count"));
				for(int i=0;i<count;i++) {
					int uniqueId = IncrementManager.INSTANCE.increase("monster");
					Monster monster = new Monster(monsterId, uniqueId, monsterName, freshTime);
					monsterMap.put(monster.getUniqueId(), monster);
				}

			}

			scene.setMonsterMap(monsterMap);
			scene.setNpcRoleMap(npcMap);
		}
		return map;
	}

//	public static final void main(String args[]) {
//		SenceXMLResolution.resolution();
//	}

}
