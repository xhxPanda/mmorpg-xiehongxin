package com.hh.mmorpg.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneDomain {

	private int id;
	private String name;
	private List<Integer> neighborSceneIds;
	private boolean canBattle;
	private boolean copy;
	private int entreNumLimit;
	private int needLevel;

	private Map<Integer, Map<Integer, Monster>> monsterSetMap;// monster初始配置的怪物生成序列

	private Map<Integer, NpcRole> npcRoleMap;

	public SceneDomain(int id, String name, String neighborScenestrs, boolean canBattle, boolean copy,
			int entreNumLimit, int needLevel) {
		this.id = id;
		this.name = name;
		this.neighborSceneIds = new ArrayList<Integer>();
		String[] strs = neighborScenestrs.split(",");
		for (String s : strs) {
			neighborSceneIds.add(Integer.parseInt(s));
		}

		this.npcRoleMap = new HashMap<>();
		this.canBattle = canBattle;
		this.copy = copy;
		this.entreNumLimit = entreNumLimit;
		this.needLevel = needLevel;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Integer> getNeighborSceneIds() {
		return neighborSceneIds;
	}

	public boolean isCanBattle() {
		return canBattle;
	}

	public boolean isCopy() {
		return copy;
	}

	public Map<Integer, NpcRole> getNpcRoleMap() {
		return npcRoleMap;
	}

	public void setNpcRoleMap(Map<Integer, NpcRole> npcRoleMap) {
		this.npcRoleMap = npcRoleMap;
	}

	public Map<Integer, Map<Integer, Monster>> getMonsterSetMap() {
		return monsterSetMap;
	}

	public void setMonsterSetMap(Map<Integer, Map<Integer, Monster>> monsterSetMap) {
		this.monsterSetMap = monsterSetMap;
	}

	public int getEntreNumLimit() {
		return entreNumLimit;
	}

	@Override
	public String toString() {
		return "SceneDomain [id=" + id + ", name=" + name + "]";
	}

}
