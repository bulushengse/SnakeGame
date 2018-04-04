package com.game;

import java.util.concurrent.locks.Lock;

import com.jni.*;

public class Food {
	public static final String FOOD = "��";  // ʳ������
	// ʳ��λ�ã����ǿ���̨�����꣬����ǽ�ڵ����λ�ã�Ĭ�ϵ��ǷǷ�λ��
	public Consoles.Position pos = new Consoles.Position(-1, -1);
	public boolean setFoodOK = false;	    // Ͷ��ʳ���Ƿ�ɹ�
	
	// Ϊ��ʵ����Ͷ��ʳ��������ߣ�ʹ��������������
	public Lock lock;                       // ��
	
	// ����Ч��
	public void animation() throws InterruptedException {
		while (setFoodOK) {
			show();         // ��ʾʳ��
			Thread.sleep(350);  // ��ʱһ��
			hide();         // ����ʳ��
			Thread.sleep(250);  // ��ʱһ��
		}
	}
	
	// ��ʾʳ��
	public void show() throws InterruptedException {
		// ��ʳ��
		// ��ͬ��
		lock.lock();    // ����
		Consoles.gotoXY(Snake.START_X + pos.x * 2, Snake.START_Y + pos.y);
		// ʳ���Ǳ�ɫ��
		Consoles.setTextColor(0xf, (int)(Math.random() * 6 + 1));	
		System.out.print(FOOD);
		lock.unlock();  // ����
	}
	
	// ����ʳ��
	public void hide() throws InterruptedException {
		// ����ʳ��
		// ��ͬ��
		lock.lock();    // ����
		Consoles.gotoXY(Snake.START_X + pos.x * 2, Snake.START_Y + pos.y);
		Consoles.setTextColor(0xf, 0xc);
		System.out.print("  ");
		lock.unlock();  // ����
	}
}