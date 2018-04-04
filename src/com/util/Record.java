package com.util;

public class Record implements Comparable<Record> {
	private int range;         // 活动范围
	private int ateFoods;      // 所吃食物数量
	private int level;         // 玩家级别
	private String nickName;   // 昵称 
	
	public Record(int range, int ateFoods, int level, String nickName) {
		this.range = range;
		this.ateFoods = ateFoods;
		this.level = level;
		this.nickName = nickName;
	}

	@Override
	public int compareTo(Record o) {
		// 比较规则：活动范围越小越排前，所吃食物越多越排前，级别不影响排名，成绩相同则 o 排后
		if (range < o.getRange()) {
			return -1;
		} else if (range > o.getRange()) {
			return 1;
		}
		
		if (ateFoods > o.getAteFoods()) {
			return -1;
		} else if (ateFoods < o.getAteFoods()) {
			return 1;
		}
		
		if (nickName.equals(o.getNickName())) {
			return 0;
		}
		
		return 1;     // 范围和食物相同但名称不同时，o 排后
	}

	@Override
	public boolean equals(Object o) {    // compareTo 方法与 equals 一致，即它们实现相同的比较逻辑
		return this.compareTo((Record)o) == 0;
	}
	
	public String toString() {
		return range + "\t" + ateFoods + "\t" + level + "\t" + nickName;
	}
	
	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getAteFoods() {
		return ateFoods;
	}

	public void setAteFoods(int ateFoods) {
		this.ateFoods = ateFoods;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
