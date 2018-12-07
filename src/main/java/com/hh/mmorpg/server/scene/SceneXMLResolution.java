package com.hh.mmorpg.server.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hh.mmorpg.Increment.IncrementManager;
import com.hh.mmorpg.domain.Monster;
import com.hh.mmorpg.domain.MonsterDomain;
import com.hh.mmorpg.domain.NpcRole;
import com.hh.mmorpg.domain.SceneDomain;

public class SceneXMLResolution {

	public static final SceneXMLResolution INSTANCE = new SceneXMLResolution();

	@SuppressWarnings("unchecked")
	public Map<Integer, SceneDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\scene.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, SceneDomain> map = new HashMap<>();

		Element rootElm = document.getRootElement();
		List<Element> sences = rootElm.elements("scene");

		// 生成怪物
		Map<Integer, MonsterDomain> monsterDomainMap = resolutionMonster();

		for (Element element : sences) {
			String name = element.attributeValue("name");

			int id = Integer.parseInt(element.attributeValue("id"));
			String neighborScenes = (element.attributeValue("neighborScenes") == null
					|| element.attributeValue("neighborScenes").isEmpty()) ? ""
							: element.attributeValue("neighborScenes");

			boolean canBattle = Boolean.parseBoolean(element.attributeValue("isCanBattle"));
			boolean copy = Boolean.parseBoolean(element.attributeValue("isCopy"));

			int entreNumLimit = Integer.parseInt(element.attributeValue("entreNumLimit"));

			SceneDomain scene = new SceneDomain(id, name, neighborScenes, canBattle, copy, entreNumLimit);
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

			Map<Integer, Map<Integer, Monster>> monsterSetMap = new HashMap<>();

			Element monstersEle = element.element("monsters");
			String monsterStr[] = monstersEle.attributeValue("monster").split("\\$");

			int teamId = 0;
			for (String monsterTeam : monsterStr) {

				String monsterIds[] = monsterTeam.split(",");

				Map<Integer, Monster> monsterMap = new HashMap<>();
				for (String monsterSet : monsterIds) {
					String[] monsterNum = monsterSet.split(":");
					int monsterId = Integer.parseInt(monsterNum[0]);
					int num = Integer.parseInt(monsterNum[1]);

					for (int i = 0; i < num; i++) {
						MonsterDomain monsterDomain = monsterDomainMap.get(monsterId);

						int uniqueId = IncrementManager.INSTANCE.increase("monster");

						boolean isNeedAi = false;
						if (copy) {
							isNeedAi = true;
						}
						Monster monster = new Monster(uniqueId, id, monsterDomain, isNeedAi);
						monsterMap.put(monster.getUniqueId(), monster);
					}

				}
				monsterSetMap.put(teamId, monsterMap);
				teamId++;
			}

			scene.setMonsterSetMap(monsterSetMap);
			scene.setNpcRoleMap(npcMap);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, MonsterDomain> resolutionMonster() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\monster.xml");

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<Integer, MonsterDomain> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> monsterEle = rootElm.elements("monster");

		for (Element element : monsterEle) {
			String name = element.attributeValue("name");
			int id = Integer.parseInt(element.attributeValue("id"));
			int freshTime = Integer.parseInt(element.attributeValue("freshTime"));
			String attributeStr = element.attributeValue("attribute");
			String skillsStr = element.attributeValue("skills");
			String killFallItemStr = element.attributeValue("fallPossibility");
			int exp = Integer.parseInt(element.attributeValue("exp"));
			MonsterDomain monsterDomain = new MonsterDomain(id, name, freshTime, attributeStr, killFallItemStr,
					skillsStr, exp);

			map.put(id, monsterDomain);
		}

		return map;
	}

	public static final void main(String args[]) {
		SceneXMLResolution.INSTANCE.resolution();
	}
}
