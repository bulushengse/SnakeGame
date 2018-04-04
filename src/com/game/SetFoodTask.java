package com.game;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SetFoodTask implements Runnable {
	private Snake snake;                // 蛇
	public Food food;                   // 投放的食物
	
	// 为了实现先投放食物、后蛇行走，使用锁和条件变量
	public Lock lock;                   // 锁
	public Condition condition;         // 条件变量
	
	public SetFoodTask(Snake snake) {
		this.snake = snake;
		food = snake.food;
	}
	
	@Override
	public void run() {
		while (!snake.gameOver) {
			// 投放食物
			setFood();
			// 显示食物动画
			try {
				food.animation();
			} catch (InterruptedException e) {
				try {  // 线程被中断时，食物消失
					food.hide();
					return;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// 投放食物，不能投放到蛇身上
	// 以下是一次投放的方法
	public void setFood() {
		lock.lock();	// 加锁
		// 随机产生合适的食物位置
		// 数组保存哪些位置被蛇占了
		boolean[][] b = new boolean[snake.range][snake.range];
		for (int i = 0; i < snake.size(); i++) {
			b[snake.get(i).x][snake.get(i).y] = true;  // 被蛇占了
		}
		int remaind = snake.range * snake.range - snake.size();  // 剩余的位置数目
		int randomIndex = (int) (Math.random() * remaind);       // 产生随机位置
		int count = 0;   // 计数器
		for (int i = 0; i < snake.range; i++) {
			for (int j = 0; j < snake.range; j++) {
				if (!b[i][j]) {
					if (count == randomIndex) {   // 找到随机位置
						food.pos.x = i;
						food.pos.y = j;
						food.setFoodOK = true;	// 投放食物成功
						condition.signal();	    // 通知等待线程
						lock.unlock();	        // 解锁
						return;
					}
					count++;
				}
			}
		}
	}
	
	/*
	// 投放食物，不能投放到蛇身上
	// 以下是试探投放的方法
	public void setFood() {
		snake.lock.lock();	// 加锁
		// 随机产生合适的食物位置
		boolean setOk = true;
		int x = 0, y = 0;
		do {
			setOk = true;	// 假定能投放成功
			// x, y 是父类中的两个公有数据成员，可以直接访问
			x = (int) (Math.random() * snake.range);
			y = (int) (Math.random() * snake.range);
			for (Consoles.Position p : snake) {
				// 有重复的位置则投放失败
				if (p.x == x && p.y == y) {		
					setOk = false;
					break;
				}
			}
		} while (!setOk);
		
		food = new Food(snake, x, y);
		setFoodOK = true;	// 投放食物成功
		snake.condition.signal();	// 通知等待线程
		snake.lock.unlock();	// 解锁
	}
	*/
}
