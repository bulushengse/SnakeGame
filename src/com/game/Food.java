package com.game;

import java.util.concurrent.locks.Lock;

import com.jni.*;

public class Food {
	public static final String FOOD = "※";  // 食物样子
	// 食物位置，不是控制台的坐标，而是墙内的相对位置，默认的是非法位置
	public Consoles.Position pos = new Consoles.Position(-1, -1);
	public boolean setFoodOK = false;	    // 投放食物是否成功
	
	// 为了实现先投放食物、后蛇行走，使用锁和条件变量
	public Lock lock;                       // 锁
	
	// 动画效果
	public void animation() throws InterruptedException {
		while (setFoodOK) {
			show();         // 显示食物
			Thread.sleep(350);  // 延时一会
			hide();         // 隐藏食物
			Thread.sleep(250);  // 延时一会
		}
	}
	
	// 显示食物
	public void show() throws InterruptedException {
		// 画食物
		// 锁同步
		lock.lock();    // 加锁
		Consoles.gotoXY(Snake.START_X + pos.x * 2, Snake.START_Y + pos.y);
		// 食物是变色的
		Consoles.setTextColor(0xf, (int)(Math.random() * 6 + 1));	
		System.out.print(FOOD);
		lock.unlock();  // 解锁
	}
	
	// 隐藏食物
	public void hide() throws InterruptedException {
		// 擦除食物
		// 锁同步
		lock.lock();    // 加锁
		Consoles.gotoXY(Snake.START_X + pos.x * 2, Snake.START_Y + pos.y);
		Consoles.setTextColor(0xf, 0xc);
		System.out.print("  ");
		lock.unlock();  // 解锁
	}
}