package com.hh.mmorpg.domain;

/**
 * 任务进度属性
 * 
 * @param value
 */
public class MissionAttribute {

	private String key;
	private String dec;
	private int pocess;
	private int max;

	public MissionAttribute(String key, String dec, int pocess, int max) {
		this.key = key;
		this.dec = dec;
		this.pocess = pocess;
		this.max = max;
	}

	public String getKey() {
		return key;
	}

	public int getPocess() {
		return pocess;
	}

	public int getMax() {
		return max;
	}

	public void changeProcess(int value) {
		this.pocess = (pocess + value) >= max ? max : (pocess + value);
	}

	public void setprocess(int process) {
		this.pocess = process;
	}

	public String getDec() {
		return dec;
	}

}