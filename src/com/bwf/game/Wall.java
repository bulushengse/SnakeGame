package com.bwf.game;

import com.bwf.jni.*;

public class Wall {
	public static final String LEFT_TOP = "┌";
	public static final String RIGHT_TOP = "┐";
	public static final String LEFT_BOTTOM = "└";
	public static final String RIGHT_BOTTOM = "┘";
	public static final String HORIZENTAL = "─";
	public static final String VERTICAL = "│";

	public static final String TITLE = "贪   吃   蛇   游   戏";	// 标题
	public static final int START_X = 6;	// 墙的左上角横坐标
	public static final int START_Y = 5;	// 墙的左上角纵坐标

	// 墙大小，横向以两个字节为单位，纵向以行为单位，包含四边在内
	public int wallSize;	

	// 构造墙对象
	// 指定墙的大小，包含四边在内
	public Wall(int wallSize) {
		this.wallSize = wallSize;
	}

	// 画墙及标题
	/*
	public void draw() {
		// 标题，要居中
		Consoles.gotoXY(START_X + wallSize - TITLE.getBytes().length / 2, START_Y - 2);
		System.out.print(TITLE);
		// 画墙，分三步
		// 1. 第一行
		Consoles.gotoXY(START_X, START_Y);	
		System.out.print(LEFT_TOP);		// 左上角
		for (int i = 0; i < wallSize - 2; i++) {	
			System.out.print(HORIZENTAL);	// 中间的横线
		}
		System.out.print(RIGHT_TOP);	// 右上角
		// 2. 中间多行
		for (int i = 1; i <= wallSize - 2; i++) {
			Consoles.gotoXY(START_X, START_Y + i);
			System.out.print(VERTICAL);	// 左边竖线
			Consoles.gotoXY(START_X + (wallSize - 1) * 2, START_Y + i);
			System.out.print(VERTICAL);	// 右边竖线
		}
		// 3. 最后一行
		Consoles.gotoXY(START_X, START_Y + wallSize - 1);
		System.out.print(LEFT_BOTTOM);	// 左下角
		for (int i = 0; i < wallSize - 2; i++) {
			System.out.print(HORIZENTAL);	// 中间的横线
		}
		System.out.print(RIGHT_BOTTOM);	// 右下角
	}
	*/

	// 另一种画墙的方法
	public void draw() {
		// 标题，要居中
		Consoles.gotoXY(START_X + wallSize - TITLE.getBytes().length / 2, START_Y - 2);
		System.out.print(TITLE);
		// 画第一行
		Consoles.gotoXY(START_X, START_Y);	
		drawALine(LEFT_TOP, HORIZENTAL, RIGHT_TOP);
		// 画中间多行
		for (int i = 1; i <= wallSize - 2; i++) {
			Consoles.gotoXY(START_X, START_Y + i);
			drawALine(VERTICAL, "  ", VERTICAL);
		}
		// 画最后一行
		Consoles.gotoXY(START_X, START_Y + wallSize - 1);
		drawALine(LEFT_BOTTOM, HORIZENTAL, RIGHT_BOTTOM);
	}

	// 画一行的方法
	public void drawALine(String left, String mid, String right) {
		System.out.print(left);
		for (int i = 0; i < wallSize - 2; i++) {
			System.out.print(mid);
		}
		System.out.print(right);
	}
}