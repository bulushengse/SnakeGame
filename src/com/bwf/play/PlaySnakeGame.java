package com.bwf.play;

import java.util.*;
import com.bwf.game.*;
import com.bwf.jni.*;

public class PlaySnakeGame {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		do {
			Consoles.setScreenColor(0xf, 0x0);	// 白底黑字
			Consoles.clearScreen();		// 清屏
			// 用户输入墙大小
			int wallSize = 0;
			while (true) {
				try {
					System.out.println("请输入墙的大小：（15 - 30）");
					wallSize = input.nextInt();
					if (wallSize < 15 || wallSize > 30) {
						System.out.println("墙的大小范围必须是：15 - 30。");
						continue;
					}
					break;
				} catch (InputMismatchException ex) {
					System.out.println("请输入整数！");
					input.nextLine();
				}
			}
			Consoles.clearScreen();	// 清屏
			// 提供蛇的初始位置，此位置是指蛇在墙内的活动范围
			// 四行表示蛇长有四节，每行第 0 列表示活动范围的列下标，
			// 第 1 列表示活动范围的行下标，行列下标从 0 开始计
			int[][] pos = {{3, 2}, {2, 2}, {1, 2}, {0, 2}};
			// 创建贪吃蛇游戏对象
			SnakeGame game = new SnakeGame(wallSize, pos, Snake.DIR_RIGHT);
			// 开始贪吃蛇游戏
			game.start();

			// 提示再玩
			Consoles.gotoXY(Wall.START_X + 4, Wall.START_Y + wallSize + 5);
			System.out.print("再玩一次吗？(y/n)");
			String yn = input.next();
			if (yn.charAt(0) == 'Y' || yn.charAt(0) == 'y') {
				continue;
			}
			System.out.println("欢迎下次再玩！");
			break;
		} while (true);
		Consoles.clearScreen();	// 清屏
		Consoles.showCursor();	// 显示光标
		input.close();
	}

}