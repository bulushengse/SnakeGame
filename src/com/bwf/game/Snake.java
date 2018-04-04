package com.bwf.game;

import com.bwf.jni.*;
import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class Snake extends LinkedList<Consoles.Position> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2047038561311874819L;
	
	// ����
	public static final String HEAD = "��";		// ��ͷ
	public static final String BODY = "��";		// ����
	public static final String TAIL = "��";		// ��β
	public static final int START_X = Wall.START_X + 2;	// �ߵĻ��Χ���ϽǺ�����
	public static final int START_Y = Wall.START_Y + 1;	// �ߵĻ��Χ���Ͻ�������
	public static final String DIR_UP    = "��";    // ����ǰ��
	public static final String DIR_DOWN  = "��";    // ����ǰ��
	public static final String DIR_LEFT  = "��";    // ����ǰ��
	public static final String DIR_RIGHT = "��";    // ����ǰ��
	// ����
	public int range;                       // �ߵĻ��Χ
	public int speed = 500;	                // �����ٶ�
	public int speedCount = 50;	            // �ٶȲ�
	public int level = 1;                   // ��Ҽ���
	public String dir;                      // ǰ������
	public int ateFoods;                    // ���µ�ʳ����
	public int ateFoodsCount = 3;           // ������ʳ���
	public boolean gameOver = false;        // ��Ϸ�Ƿ����
	public Food food;                       // ʳ��
	
	// Ϊ��ʵ����Ͷ��ʳ��������ߣ�ʹ��������������
	public Lock lock;                       // ��
	public Condition condition;             // ��������

	// ����̰����
	// ָ���ߵ�ÿ�ڳ�ʼλ�á��ߵĳ�ʼ�н�����
	public Snake(int[][] positions, int range, String dir) {
		// ��ͷ�� 0 �±꿪ʼ����β�� size() - 1 ��
		for (int i = 0; i < positions.length; i++) {
			add(new Consoles.Position(positions[i][0], positions[i][1]));
		}
		this.range = range;
		this.dir = dir;
	}

	// ��ʼ���߻���
	public void initView() {
		// ����ͷ
		gotoXY(peekFirst());
		System.out.print(HEAD);
		// ������
		for (int i = 1; i < size() - 1; i++) {
			gotoXY(get(i));
			System.out.print(BODY);
		}
		// ����β
		gotoXY(peekLast());
		System.out.print(TAIL);
	}

	// ��ӡ��ʾ��Ϣ
	public void printInfo() {
		lock.lock();
		gotoXY(range + 2, 1);
		System.out.print("����" + level);
		gotoXY(range + 2, 3);
		System.out.print("�ٶȣ�" + speed);
		gotoXY(range + 2, 5);
		System.out.print("�Ե�ʳ������" + ateFoods);
		gotoXY(range + 2, 7);
		System.out.print("<p> ����ͣ��Ϸ");
		gotoXY(range + 2, 9);
		System.out.print("<q> ���˳�������Ϸ");
		lock.unlock();
	}

	// ����һ��
	public void oneStep() {
		// ��ȡ��ͷλ��
		int x = peekFirst().x, y = peekFirst().y;
		// �����ߵ�ǰ�����������ͷ��һ����λ��
		switch (dir) {
		case DIR_UP:
			// (y + range - 1) % range ������ʽ���Դ���ǽ
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

		// �Ե�ʳ���ˣ�
		if (x == food.pos.x && y == food.pos.y) {
			grownStep(x, y);
			return;   // �Ե�ʳ�������Ͷ��ʳ�����ʱ��
		// ���������ˣ�
		} else if (ateSelf(x, y)) {
			lock.lock();
			gotoXY(0, range + 4);
			System.out.println("�Ե��Լ��ˡ���Ϸ������");
			lock.unlock();
			gameOver = true;
			return;
		// �ǿ�λ��
		} else {
			// ������һ��
			mormalStep(x, y);
		}
				
		// ��ͣһ��
		try {
			Thread.sleep(speed);
		} catch (InterruptedException ex) {	}
	}

	// �Ե��Լ���
	public boolean ateSelf(int x, int y) {
		for (Consoles.Position p : this) {
			if (p.x == x && p.y == y) {		// �Ե��Լ�
				return true;
			}
		}
		return false;	// û�Ե��Լ�
	}

	// ������һ��
	public void mormalStep(int x, int y) {
		// ��ͷǰ��һ��
		headStep(x, y);
		// ��βǰ��һ��
		tailStep();
		
		// ����ÿ��λ��
		// 1. ɾ����β
		removeLast();
		// 2. ��ͷǰ����һ�ڣ���� x,y λ��
		addFirst(new Consoles.Position(x, y));
	}

	// ����һ�ڣ�ֻ����ͷ��������β
	public void grownStep(int x, int y) {
		// ��ͷǰ��һ��
		headStep(x, y);
		// ��ͷǰ����һ�ڣ���� x,y λ��
		addFirst(new Consoles.Position(x, y));
		// �Ե���ʳ������ 1
		ateFoods++;

		// ���ѳԹ�����ʳ�ռ��������ǽ�ռ��ˣ���Ϸ����
		if (ateFoods == range * range) {
			lock.lock();
			gotoXY(1, range + 4);
			System.out.print("���ѳԹ�����ʳ���ˡ����Ǹ��صص�����̰���ߣ���Ϸ������");
			lock.unlock();
			gameOver = true;
			return;
		}

		// ������Ҽ�����ߵ������ٶ�
		if (ateFoods % ateFoodsCount == 0) {
			level++;	// ��һ��
			// ���٣�����ٶ�Ϊ 30 ms
			speed = speed <= speedCount ? 30 : speed - speedCount;	
		}
		
		// ʳ����ʧ
		lock.lock();
		food.setFoodOK = false;
		lock.unlock();
		
		// ������ʾ��Ϣ
		printInfo();
	}

	// ��ͷǰ��һ��, x �� y ����ͷ��λ������
	public void headStep(int x, int y) {
		lock.lock();
		// 1.��λ�û���ͷ
		gotoXY(x, y);
		Consoles.setTextColor(0xf, 0xc);
		System.out.print(HEAD);
		Consoles.setTextColor(0xf, 0x0);
		// 2. ԭ��ͷλ�û�����
		gotoXY(peekFirst());
		System.out.print(BODY);
		lock.unlock();
	}
	
	// ��βǰ��һ��
	public void tailStep() {
		lock.lock();
		// 1. ��λ�û���β
		gotoXY(get(size() - 2));
		Consoles.setTextColor(0xf, 0xd);
		System.out.print(TAIL);
		Consoles.setTextColor(0xf, 0x0);
		// 2. ԭ��βλ�ò���
		gotoXY(peekLast());
		System.out.print("  ");
		lock.unlock();
	}
	
	// ���� gotoXY ������ʵ�ַ���Ķ�λ
	// ��λ�� x, y λ�� ---- ע�⣺x, y ������Ļ���꣬���ߵĻ��Χ������λ��
	public void gotoXY(int x, int y) {
		Consoles.gotoXY(START_X + x * 2, START_Y + y);
	}

	// ��λ�� Consoles.Position ����ʾ��λ�� ---- 
	// ע�⣺��λ�á�������Ļ���꣬���ߵĻ��Χ������λ��
	public void gotoXY(Consoles.Position p) {
		Consoles.gotoXY(START_X + p.x * 2, START_Y + p.y);
	}
}