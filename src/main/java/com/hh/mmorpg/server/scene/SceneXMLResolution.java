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

	public Map<Integer, SceneDomain> resolution() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\scene.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		Map<Integer, SceneDomain> map = new HashMap<>();

		Element rootElm = document.getRootElement();
		List<Element> sences = rootElm.elements("scene");

		// 怪物map
		Map<Integer, MonsterDomain> monsterDomainMap = resolutionMonster();
		Map<Integer, NpcRole> npcDomainMap = getResolutionNpc();

		for (Element element : sences) {
			String name = element.attributeValue("name");

			int id = Integer.parseInt(element.attributeValue("id"));
			String neighborScenes = (element.attributeValue("neighborScenes") == null
					|| element.attributeValue("neighborScenes").isEmpty()) ? ""
							: element.attributeValue("neighborScenes");

			boolean canBattle = Boolean.parseBoolean(element.attributeValue("isCanBattle"));
			boolean copy = Boolean.parseBoolean(element.attributeValue("isCopy"));

			int entreNumLimit = Integer.parseInt(element.attributeValue("entreNumLimit"));
			int needLevel = Integer.parseInt(element.attributeValue("needLevel"));
			SceneDomain scene = new SceneDomain(id, name, neighborScenes, canBattle, copy, entreNumLimit, needLevel);
			map.put(scene.getId(), scene);

			// 生成npc
			Map<Integer, NpcRole> npcMap = new HashMap<>();
			Element npcEle = element.element("npcs");
			if (!npcEle.attributeValue("npc").isEmpty()) {
				String npcStr[] = npcEle.attributeValue("npc").split(",");
				for (String npcId : npcStr) {
					npcMap.put(Integer.parseInt(npcId), npcDomainMap.get(Integer.parseInt(npcId)));
				}
			}

			// 生成场景中怪物
			Map<Integer, Map<Integer, Monster>> monsterSetMap = new HashMap<>();

			Element monstersEle = element.element("monsters");
			if (!monstersEle.attributeValue("monster").isEmpty()) {
				int teamId = 0;
				String monsterStr[] = monstersEle.attributeValue("monster").split("\\$");
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
			String lastAttackBonus = element.attributeValue("lastAttackBonus");
			int exp = Integer.parseInt(element.attributeValue("exp"));
			MonsterDomain monsterDomain = new MonsterDomain(id, name, freshTime, attributeStr, killFallItemStr,
					skillsStr, exp, lastAttackBonus);

			map.put(id, monsterDomain);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	private Map<Integer, NpcRole> getResolutionNpc() {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read("..\\mmorpg\\docs\\xml\\npc.xml");

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		Map<Integer, NpcRole> map = new HashMap<>();
		Element rootElm = document.getRootElement();
		List<Element> npcEle = rootElm.elements("npc");
		for (Element element : npcEle) {
			String name = element.attributeValue("name");
			int id = Integer.parseInt(element.attributeValue("id"));
			String talk = element.attributeValue("talk");
			NpcRole npcRole = new NpcRole(id, name, talk);

			map.put(id, npcRole);
		}
		return map;
	}

	public static final void main(String args[]) {
		SceneXMLResolution.INSTANCE.resolution();
	}
}
