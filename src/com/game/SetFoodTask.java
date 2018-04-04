package com.game;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SetFoodTask implements Runnable {
	private Snake snake;                // ��
	public Food food;                   // Ͷ�ŵ�ʳ��
	
	// Ϊ��ʵ����Ͷ��ʳ��������ߣ�ʹ��������������
	public Lock lock;                   // ��
	public Condition condition;         // ��������
	
	public SetFoodTask(Snake snake) {
		this.snake = snake;
		food = snake.food;
	}
	
	@Override
	public void run() {
		while (!snake.gameOver) {
			// Ͷ��ʳ��
			setFood();
			// ��ʾʳ�ﶯ��
			try {
				food.animation();
			} catch (InterruptedException e) {
				try {  // �̱߳��ж�ʱ��ʳ����ʧ
					food.hide();
					return;
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// Ͷ��ʳ�����Ͷ�ŵ�������
	// ������һ��Ͷ�ŵķ���
	public void setFood() {
		lock.lock();	// ����
		// ����������ʵ�ʳ��λ��
		// ���鱣����Щλ�ñ���ռ��
		boolean[][] b = new boolean[snake.range][snake.range];
		for (int i = 0; i < snake.size(); i++) {
			b[snake.get(i).x][snake.get(i).y] = true;  // ����ռ��
		}
		int remaind = snake.range * snake.range - snake.size();  // ʣ���λ����Ŀ
		int randomIndex = (int) (Math.random() * remaind);       // �������λ��
		int count = 0;   // ������
		for (int i = 0; i < snake.range; i++) {
			for (int j = 0; j < snake.range; j++) {
				if (!b[i][j]) {
					if (count == randomIndex) {   // �ҵ����λ��
						food.pos.x = i;
						food.pos.y = j;
						food.setFoodOK = true;	// Ͷ��ʳ��ɹ�
						condition.signal();	    // ֪ͨ�ȴ��߳�
						lock.unlock();	        // ����
						return;
					}
					count++;
				}
			}
		}
	}
	
	/*
	// Ͷ��ʳ�����Ͷ�ŵ�������
	// ��������̽Ͷ�ŵķ���
	public void setFood() {
		snake.lock.lock();	// ����
		// ����������ʵ�ʳ��λ��
		boolean setOk = true;
		int x = 0, y = 0;
		do {
			setOk = true;	// �ٶ���Ͷ�ųɹ�
			// x, y �Ǹ����е������������ݳ�Ա������ֱ�ӷ���
			x = (int) (Math.random() * snake.range);
			y = (int) (Math.random() * snake.range);
			for (Consoles.Position p : snake) {
				// ���ظ���λ����Ͷ��ʧ��
				if (p.x == x && p.y == y) {		
					setOk = false;
					break;
				}
			}
		} while (!setOk);
		
		food = new Food(snake, x, y);
		setFoodOK = true;	// Ͷ��ʳ��ɹ�
		snake.condition.signal();	// ֪ͨ�ȴ��߳�
		snake.lock.unlock();	// ����
	}
	*/
}
