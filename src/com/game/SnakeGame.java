package com.game;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jni.Consoles;
import com.util.Ranking;
import com.util.Record;

public class SnakeGame {
	public Wall wall;		// 墙
	public Snake snake;		// 蛇
	public Food food;       // 食物
	
	// 为了实现先投放食物、后蛇行走，使用锁和条件变量
	public Lock lock = new ReentrantLock();	            // 锁
	public Condition condition = lock.newCondition();	// 条件变量
	
	// 构造贪吃蛇游戏对象
	// 指定大小的墙、蛇的初始长度、蛇的每节初始位置、蛇的初始行进方向
	public SnakeGame(int wallSize, int[][] positions, String dir) {
		if (!isValide(positions, wallSize - 2)) {
			throw new IllegalArgumentException("蛇坐标不合法");
		}
		wall = new Wall(wallSize);
		snake = new Snake(positions, wallSize - 2, dir);
		food = new Food();
	}

	// 开始贪吃蛇游戏
	public void start() {
		// 画墙及标题、提示
		wall.draw();

		// 开始游戏
		playGame();
	}
	
	// 检测蛇初始化位置的合法性
	// 1. 蛇的每节必须在墙内
	// 2. 不能有任意两节位置相同
	// 3. 从蛇头开始，必须一节一节相连，不能断开
	public boolean isValide(int[][] positions, int range) {
		// 1. 蛇的每节必须在墙内
		if (!isInRange(positions, range)) {
			return false;
		}
		// 2. 不能有任意两节位置相同
		if (hasSameNode(positions)) {
			return false;
		}
		// 3. 从蛇头开始，必须一节一节相连，不能断开
		if (!isContinuous(positions)) {
			return false;
		}
		return true;
	}
	
	// 1. 蛇的每节必须在墙内
	// 在墙内返回 true，不在墙内返回 false
	public boolean isInRange(int[][] positions, int range) {
		// 遍历蛇的每一节
		for (int[] p : positions) {
			// 不在范围内则说明不在墙内
			if (p[0] < 0 || p[0] >= range || p[1] < 0 || p[1] >= range) {
				return false;
			}
		}
		return true;
	}
	
	// 2. 不能有任意两节位置相同
	// 有两节位置相同返回 true，没有任意两节位置相同返回 false
	public boolean hasSameNode(int[][] positions) {
		// 从第 0 节开始，每节与其后的所有节的位置进行比较
		for (int i = 0; i < positions.length - 1; i++) {
			for (int j = i + 1; j < positions.length; j++) {
				if ((positions[i][0] == positions[j][0]) &&
					(positions[i][1] == positions[j][1])) {
					return true;  // 有两节位置相同
				}
			}
		}
		return false;  // 没有任意两节位置相同
	}
	
	// 3. 从蛇头开始，必须一节一节相连，不能断开
	// 是一节一节相连的返回 true，有断开的返回 false
	public boolean isContinuous(int[][] positions) {
		// 从第 1 节开始，每一节必须与前一节相连
		for (int i = 1; i < positions.length; i++) {
			// 横坐标相同时，则纵坐标只能相差1
			if (positions[i][0] == positions[i - 1][0]) {  
				if ((positions[i][1] != positions[i - 1][1] + 1) &&
					(positions[i][1] != positions[i - 1][1] - 1)) {
					return false;
				}
			// 纵坐标相同时，则横坐标只能相差1
			} else if (positions[i][1] == positions[i - 1][1]) {
				if ((positions[i][0] != positions[i - 1][0] + 1) &&
					(positions[i][0] != positions[i - 1][0] - 1)) {
						return false;
				}
			}
		}
		return true;
	}
	
	// 开始游戏
	public void playGame() {
		// 隐藏光标
		Consoles.hideCursor();
		
		snake.lock = lock;
		snake.condition = condition;
		snake.food = food;
		food.lock = lock;
		//food.condition = condition;
		
		// 初始化蛇画面
		snake.initView();
		// 打印提示信息
		snake.printInfo();
		
		SetFoodTask setFoodTask = new SetFoodTask(snake);
		setFoodTask.lock = lock;
		setFoodTask.condition = condition;
		// 启动投放食物线程
		Thread thread = new Thread(setFoodTask);
		thread.start();
		
		// 游戏没有结束，蛇不停前进
		while (!snake.gameOver) {
			lock.lock();
			try {
				while (!food.setFoodOK) {   // 等待投放食物
					condition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.unlock();
			
			// 检测并处理按键，根据按键来修改蛇的前进方向
			checkKey();
			// 蛇走一步
			snake.oneStep();
		}

		// 结束食物线程
		thread.interrupt();
		// 稍等一会
		try {
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//Consoles.setTextColor(0xf, 0x0);   // 恢复白底黑字
		
		// 显示光标
		Consoles.showCursor();
		try {
			record();   // 记录成绩
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// 检测并处理按键
	public void checkKey() {
		// 以下使用 Consoles.getPressedKey() 方法获取按键
		int keyValue = Consoles.getPressedKey();
		if (keyValue != Consoles.NONE) {  // 有键按下
			switch (keyValue) {
			case Consoles.UP:     // 上箭头
				snake.dir = Snake.DIR_UP;
				break;
			case Consoles.DOWN:   // 下箭头
				snake.dir = Snake.DIR_DOWN;
				break;
			case Consoles.LEFT:   // 左箭头
				snake.dir = Snake.DIR_LEFT;
				break;
			case Consoles.RIGHT:  // 右箭头
				snake.dir = Snake.DIR_RIGHT;	
				break;
			case 'p':
			case 'P':
				while (Consoles.getPressedKey() == Consoles.NONE);
				break;
			case 'q':
			case 'Q':
				lock.lock();
				snake.gotoXY(1, snake.range + 4);
				System.out.print("确定退出本次游戏吗？(y/n)");
				lock.unlock();
				int answer = 0;
				// 等待输入按键
				while ((answer = Consoles.getPressedKey()) == Consoles.NONE);
				if (answer == 'y' || answer == 'Y') {
					snake.gameOver = true;
					return;
				}
				lock.lock();
				snake.gotoXY(1, snake.range + 4);
				System.out.print("                                        ");
				lock.unlock();
			}
		}
	}
	
	// 以下方法使用 Ranking 和 Record 类处理记录
	public void record() throws FileNotFoundException {
		Ranking ranking = new Ranking();
		String filename = "record.txt";
		ranking.loadRecord(filename);
		
		// 新的记录，默认没有昵称
		Record record = new Record(snake.range, snake.ateFoods, snake.level, "");
		// 显示历史前三名的排名
		showRank(ranking.get(record.getRange()), 3);
		
		// 没吃食物，则退出
		if (snake.ateFoods == 0) {
			return;
		}
		
		// 获取当前记录的新排名
		int rank = ranking.checkRank(record);
		// 排名在前三名才记录到文件中
		if (rank <= 3) {
			getNickName(record, rank);       // 获取昵称
			ranking.addRecord(record);       // 增加到排行榜
			ranking.deleteRecord(record.getRange(), 3);    // 只保存 3 条记录
			ranking.saveRecord(filename);    // 保存到文件
		}
	}
	
	// 显示前 n 名
	public void showRank(TreeSet<Record> set, int n) {
		int rank = 0;            // 名次，默认没有排名		
		snake.gotoXY(snake.range + 2, 11);
		System.out.print("\t历史排名：");
		snake.gotoXY(snake.range + 2, 12);
		System.out.print("\t名次\t范围\t食物\t级别\t名字");
		if (set == null) {
			return;
		}
		Iterator<Record> it = set.iterator();
		if (n > set.size()) {   // 不够 n 名则以实际为准
			n = set.size();
		}
		while (n > 0) {
			snake.gotoXY(snake.range + 2, 13 + rank);
			System.out.println("\t" + ++rank + "\t" + it.next());
			n--;
		}
	}
	
	// 获取用户昵称
	public void getNickName(Record record, int rank) {
		record.setNickName("匿名");         // 默认为"匿名"
		snake.gotoXY(snake.range + 2, 16);
		System.out.println("\t您的排名是：" + rank);
		snake.gotoXY(snake.range + 2, 17);
		System.out.print("\t要记录你的名字（昵称）吗？");
		Scanner input = new Scanner(System.in);
		char yn = input.next().charAt(0);
		if (yn == 'Y' || yn == 'y') {
			snake.gotoXY(snake.range + 2, 18);
			System.out.print("\t请输入您的名字（昵称）：");
			String nickName = input.next();
			record.setNickName(nickName);
		}
	}

}



