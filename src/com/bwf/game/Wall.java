package com.bwf.game;

import com.bwf.jni.*;

public class Wall {
	public static final String LEFT_TOP = "��";
	public static final String RIGHT_TOP = "��";
	public static final String LEFT_BOTTOM = "��";
	public static final String RIGHT_BOTTOM = "��";
	public static final String HORIZENTAL = "��";
	public static final String VERTICAL = "��";

	public static final String TITLE = "̰   ��   ��   ��   Ϸ";	// ����
	public static final int START_X = 6;	// ǽ�����ϽǺ�����
	public static final int START_Y = 5;	// ǽ�����Ͻ�������

	// ǽ��С�������������ֽ�Ϊ��λ����������Ϊ��λ�������ı�����
	public int wallSize;	

	// ����ǽ����
	// ָ��ǽ�Ĵ�С�������ı�����
	public Wall(int wallSize) {
		this.wallSize = wallSize;
	}

	// ��ǽ������
	/*
	public void draw() {
		// ���⣬Ҫ����
		Consoles.gotoXY(START_X + wallSize - TITLE.getBytes().length / 2, START_Y - 2);
		System.out.print(TITLE);
		// ��ǽ��������
		// 1. ��һ��
		Consoles.gotoXY(START_X, START_Y);	
		System.out.print(LEFT_TOP);		// ���Ͻ�
		for (int i = 0; i < wallSize - 2; i++) {	
			System.out.print(HORIZENTAL);	// �м�ĺ���
		}
		System.out.print(RIGHT_TOP);	// ���Ͻ�
		// 2. �м����
		for (int i = 1; i <= wallSize - 2; i++) {
			Consoles.gotoXY(START_X, START_Y + i);
			System.out.print(VERTICAL);	// �������
			Consoles.gotoXY(START_X + (wallSize - 1) * 2, START_Y + i);
			System.out.print(VERTICAL);	// �ұ�����
		}
		// 3. ���һ��
		Consoles.gotoXY(START_X, START_Y + wallSize - 1);
		System.out.print(LEFT_BOTTOM);	// ���½�
		for (int i = 0; i < wallSize - 2; i++) {
			System.out.print(HORIZENTAL);	// �м�ĺ���
		}
		System.out.print(RIGHT_BOTTOM);	// ���½�
	}
	*/

	// ��һ�ֻ�ǽ�ķ���
	public void draw() {
		// ���⣬Ҫ����
		Consoles.gotoXY(START_X + wallSize - TITLE.getBytes().length / 2, START_Y - 2);
		System.out.print(TITLE);
		// ����һ��
		Consoles.gotoXY(START_X, START_Y);	
		drawALine(LEFT_TOP, HORIZENTAL, RIGHT_TOP);
		// ���м����
		for (int i = 1; i <= wallSize - 2; i++) {
			Consoles.gotoXY(START_X, START_Y + i);
			drawALine(VERTICAL, "  ", VERTICAL);
		}
		// �����һ��
		Consoles.gotoXY(START_X, START_Y + wallSize - 1);
		drawALine(LEFT_BOTTOM, HORIZENTAL, RIGHT_BOTTOM);
	}

	// ��һ�еķ���
	public void drawALine(String left, String mid, String right) {
		System.out.print(left);
		for (int i = 0; i < wallSize - 2; i++) {
			System.out.print(mid);
		}
		System.out.print(right);
	}
}