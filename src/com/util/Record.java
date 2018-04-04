package com.util;

public class Record implements Comparable<Record> {
	private int range;         // ���Χ
	private int ateFoods;      // ����ʳ������
	private int level;         // ��Ҽ���
	private String nickName;   // �ǳ� 
	
	public Record(int range, int ateFoods, int level, String nickName) {
		this.range = range;
		this.ateFoods = ateFoods;
		this.level = level;
		this.nickName = nickName;
	}

	@Override
	public int compareTo(Record o) {
		// �ȽϹ��򣺻��ΧԽСԽ��ǰ������ʳ��Խ��Խ��ǰ������Ӱ���������ɼ���ͬ�� o �ź�
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
		
		return 1;     // ��Χ��ʳ����ͬ�����Ʋ�ͬʱ��o �ź�
	}

	@Override
	public boolean equals(Object o) {    // compareTo ������ equals һ�£�������ʵ����ͬ�ıȽ��߼�
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
