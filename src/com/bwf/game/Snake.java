package com.bwf.game;

import com.bwf.jni.*;
import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class Snake extends LinkedList<Consoles.Position> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2047038561311874819L;
	
	// 常量
	public static final String HEAD = "¤";		// 蛇头
	public static final String BODY = "○";		// 蛇身
	public static final String TAIL = "◎";		// 蛇尾
	public static final int START_X = Wall.START_X + 2;	// 蛇的活动范围左上角横坐标
	public static final int START_Y = Wall.START_Y + 1;	// 蛇的活动范围左上角纵坐标
	public static final String DIR_UP    = "上";    // 向上前进
	public static final String DIR_DOWN  = "下";    // 向下前进
	public static final String DIR_LEFT  = "左";    // 向左前进
	public static final String DIR_RIGHT = "右";    // 向右前进
	// 变量
	public int range;                       // 蛇的活动范围
	public int speed = 500;	                // 爬行速度
	public int speedCount = 50;	            // 速度差
	public int level = 1;                   // 玩家级别
	public String dir;                      // 前进方向
	public int ateFoods;                    // 吃下的食物数
	public int ateFoodsCount = 3;           // 升级的食物差
	public boolean gameOver = false;        // 游戏是否结束
	public Food food;                       // 食物
	
	// 为了实现先投放食物、后蛇行走，使用锁和条件变量
	public Lock lock;                       // 锁
	public Condition condition;             // 条件变量

	// 构造贪吃蛇
	// 指定蛇的每节初始位置、蛇的初始行进方向
	public Snake(int[][] positions, int range, String dir) {
		// 蛇头从 0 下标开始，蛇尾在 size() - 1 处
		for (int i = 0; i < positions.length; i++) {
			add(new Consoles.Position(positions[i][0], positions[i][1]));
		}
		this.range = range;
		this.dir = dir;
	}

	// 初始化蛇画面
	public void initView() {
		// 画蛇头
		gotoXY(peekFirst());
		System.out.print(HEAD);
		// 画蛇身
		for (int i = 1; i < size() - 1; i++) {
			gotoXY(get(i));
			System.out.print(BODY);
		}
		// 画蛇尾
		gotoXY(peekLast());
		System.out.print(TAIL);
	}

	// 打印提示信息
	public void printInfo() {
		lock.lock();
		gotoXY(range + 2, 1);
		System.out.print("级别：" + level);
		gotoXY(range + 2, 3);
		System.out.print("速度：" + speed);
		gotoXY(range + 2, 5);
		System.out.print("吃的食物数：" + ateFoods);
		gotoXY(range + 2, 7);
		System.out.print("<p> 键暂停游戏");
		gotoXY(range + 2, 9);
		System.out.print("<q> 键退出本次游戏");
		lock.unlock();
	}

	// 蛇走一步
	public void oneStep() {
		// 获取蛇头位置
		int x = peekFirst().x, y = peekFirst().y;
		// 根据蛇的前进方向计算蛇头下一步的位置
		switch (dir) {
		case DIR_UP:
			// (y + range - 1) % range 这个表达式可以处理穿墙
			y = (y + range - 1) % range;
			break;
		case DIR_DOWN:
			y = (y + range + 1) % range;
			break;
		case DIR_LEFT:
			x = (x + range - 1) % range;
			break;
		case DIR_RIGHT:
			x = (x + range + 1) % range;
			break;
		}

		// 吃到食物了？
		if (x == food.pos.x && y == food.pos.y) {
			grownStep(x, y);
			return;   // 吃到食物就马上投放食物，不延时了
		// 碰到身子了？
		} else if (ateSelf(x, y)) {
			lock.lock();
			gotoXY(0, range + 4);
			System.out.println("吃到自己了。游戏结束。");
			lock.unlock();
			gameOver = true;
			return;
		// 是空位置
		} else {
			// 正常走一步
			mormalStep(x, y);
		}
				
		// 暂停一会
		try {
			Thread.sleep(speed);
		} catch (InterruptedException ex) {	}
	}

	// 吃到自己？
	public boolean ateSelf(int x, int y) {
		for (Consoles.Position p : this) {
			if (p.x == x && p.y == y) {		// 吃到自己
				return true;
			}
		}
		return false;	// 没吃到自己
	}

	// 正常走一步
	public void mormalStep(int x, int y) {
		// 蛇头前进一步
		headStep(x, y);
		// 蛇尾前进一步
		tailStep();
		
		// 调整每节位置
		// 1. 删除蛇尾
		removeLast();
		// 2. 蛇头前增加一节，添加 x,y 位置
		addFirst(new Consoles.Position(x, y));
	}

	// 蛇身长一节，只画蛇头，不画蛇尾
	public void grownStep(int x, int y) {
		// 蛇头前进一步
		headStep(x, y);
		// 蛇头前增加一节，添加 x,y 位置
		addFirst(new Consoles.Position(x, y));
		// 吃掉的食物数加 1
		ateFoods++;

		// 蛇已吃光所有食物，占满整个内墙空间了，游戏结束
		if (ateFoods == range * range) {
			lock.lock();
			gotoXY(1, range + 4);
			System.out.print("蛇已吃光所有食物了。真是个地地道道的贪吃蛇！游戏结束。");
			lock.unlock();
			gameOver = true;
			return;
		}

		// 调整玩家级别和蛇的行走速度
		if (ateFoods % ateFoodsCount == 0) {
			level++;	// 升一级
			// 提速，最高速度为 30 ms
			speed = speed <= speedCount ? 30 : speed - speedCount;	
		}
		
		// 食物消失
		lock.lock();
		food.setFoodOK = false;
		lock.unlock();
		
		// 更新提示信息
		printInfo();
	}

	// 蛇头前进一步, x 和 y 是蛇头新位置坐标
	public void headStep(int x, int y) {
		lock.lock();
		// 1.新位置画蛇头
		gotoXY(x, y);
		Consoles.setTextColor(0xf, 0xc);
		System.out.print(HEAD);
		Consoles.setTextColor(0xf, 0x0);
		// 2. 原蛇头位置画蛇身
		gotoXY(peekFirst());
		System.out.print(BODY);
		lock.unlock();
	}
	
	// 蛇尾前进一步
	public void tailStep() {
		lock.lock();
		// 1. 新位置画蛇尾
		gotoXY(get(size() - 2));
		Consoles.setTextColor(0xf, 0xd);
		System.out.print(TAIL);
		Consoles.setTextColor(0xf, 0x0);
		// 2. 原蛇尾位置擦除
		gotoXY(peekLast());
		System.out.print("  ");
		lock.unlock();
	}
	
	// 重载 gotoXY 方法，实现方便的定位
	// 定位到 x, y 位置 ---- 注意：x, y 不是屏幕坐标，是蛇的活动范围的行列位置
	public void gotoXY(int x, int y) {
		Consoles.gotoXY(START_X + x * 2, START_Y + y);
	}

	// 定位到 Consoles.Position 所表示的位置 ---- 
	// 注意：“位置”不是屏幕坐标，是蛇的活动范围的行列位置
	public void gotoXY(Consoles.Position p) {
		Consoles.gotoXY(START_X + p.x * 2, START_Y + p.y);
	}
}