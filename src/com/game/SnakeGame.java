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
	public Wall wall;		// ǽ
	public Snake snake;		// ��
	public Food food;       // ʳ��
	
	// Ϊ��ʵ����Ͷ��ʳ��������ߣ�ʹ��������������
	public Lock lock = new ReentrantLock();	            // ��
	public Condition condition = lock.newCondition();	// ��������
	
	// ����̰������Ϸ����
	// ָ����С��ǽ���ߵĳ�ʼ���ȡ��ߵ�ÿ�ڳ�ʼλ�á��ߵĳ�ʼ�н�����
	public SnakeGame(int wallSize, int[][] positions, String dir) {
		if (!isValide(positions, wallSize - 2)) {
			throw new IllegalArgumentException("�����겻�Ϸ�");
		}
		wall = new Wall(wallSize);
		snake = new Snake(positions, wallSize - 2, dir);
		food = new Food();
	}

	// ��ʼ̰������Ϸ
	public void start() {
		// ��ǽ�����⡢��ʾ
		wall.draw();

		// ��ʼ��Ϸ
		playGame();
	}
	
	// ����߳�ʼ��λ�õĺϷ���
	// 1. �ߵ�ÿ�ڱ�����ǽ��
	// 2. ��������������λ����ͬ
	// 3. ����ͷ��ʼ������һ��һ�����������ܶϿ�
	public boolean isValide(int[][] positions, int range) {
		// 1. �ߵ�ÿ�ڱ�����ǽ��
		if (!isInRange(positions, range)) {
			return false;
		}
		// 2. ��������������λ����ͬ
		if (hasSameNode(positions)) {
			return false;
		}
		// 3. ����ͷ��ʼ������һ��һ�����������ܶϿ�
		if (!isContinuous(positions)) {
			return false;
		}
		return true;
	}
	
	// 1. �ߵ�ÿ�ڱ�����ǽ��
	// ��ǽ�ڷ��� true������ǽ�ڷ��� false
	public boolean isInRange(int[][] positions, int range) {
		// �����ߵ�ÿһ��
		for (int[] p : positions) {
			// ���ڷ�Χ����˵������ǽ��
			if (p[0] < 0 || p[0] >= range || p[1] < 0 || p[1] >= range) {
				return false;
			}
		}
		return true;
	}
	
	// 2. ��������������λ����ͬ
	// ������λ����ͬ���� true��û����������λ����ͬ���� false
	public boolean hasSameNode(int[][] positions) {
		// �ӵ� 0 �ڿ�ʼ��ÿ�����������нڵ�λ�ý��бȽ�
		for (int i = 0; i < positions.length - 1; i++) {
			for (int j = i + 1; j < positions.length; j++) {
				if ((positions[i][0] == positions[j][0]) &&
					(positions[i][1] == positions[j][1])) {
					return true;  // ������λ����ͬ
				}
			}
		}
		return false;  // û����������λ����ͬ
	}
	
	// 3. ����ͷ��ʼ������һ��һ�����������ܶϿ�
	// ��һ��һ�������ķ��� true���жϿ��ķ��� false
	public boolean isContinuous(int[][] positions) {
		// �ӵ� 1 �ڿ�ʼ��ÿһ�ڱ�����ǰһ������
		for (int i = 1; i < positions.length; i++) {
			// ��������ͬʱ����������ֻ�����1
			if (positions[i][0] == positions[i - 1][0]) {  
				if ((positions[i][1] != positions[i - 1][1] + 1) &&
					(positions[i][1] != positions[i - 1][1] - 1)) {
					return false;
				}
			// ��������ͬʱ���������ֻ�����1
			} else if (positions[i][1] == positions[i - 1][1]) {
				if ((positions[i][0] != positions[i - 1][0] + 1) &&
					(positions[i][0] != positions[i - 1][0] - 1)) {
						return false;
				}
			}
		}
		return true;
	}
	
	// ��ʼ��Ϸ
	public void playGame() {
		// ���ع��
		Consoles.hideCursor();
		
		snake.lock = lock;
		snake.condition = condition;
		snake.food = food;
		food.lock = lock;
		//food.condition = condition;
		
		// ��ʼ���߻���
		snake.initView();
		// ��ӡ��ʾ��Ϣ
		snake.printInfo();
		
		SetFoodTask setFoodTask = new SetFoodTask(snake);
		setFoodTask.lock = lock;
		setFoodTask.condition = condition;
		// ����Ͷ��ʳ���߳�
		Thread thread = new Thread(setFoodTask);
		thread.start();
		
		// ��Ϸû�н������߲�ͣǰ��
		while (!snake.gameOver) {
			lock.lock();
			try {
				while (!food.setFoodOK) {   // �ȴ�Ͷ��ʳ��
					condition.await();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock.unlock();
			
			// ��Ⲣ�����������ݰ������޸��ߵ�ǰ������
			checkKey();
			// ����һ��
			snake.oneStep();
		}

		// ����ʳ���߳�
		thread.interrupt();
		// �Ե�һ��
		try {
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//Consoles.setTextColor(0xf, 0x0);   // �ָ��׵׺���
		
		// ��ʾ���
		Consoles.showCursor();
		try {
			record();   // ��¼�ɼ�
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// ��Ⲣ������
	public void checkKey() {
		// ����ʹ�� Consoles.getPressedKey() ������ȡ����
		int keyValue = Consoles.getPressedKey();
		if (keyValue != Consoles.NONE) {  // �м�����
			switch (keyValue) {
			case Consoles.UP:     // �ϼ�ͷ
				snake.dir = Snake.DIR_UP;
				break;
			case Consoles.DOWN:   // �¼�ͷ
				snake.dir = Snake.DIR_DOWN;
				break;
			case Consoles.LEFT:   // ���ͷ
				snake.dir = Snake.DIR_LEFT;
				break;
			case Consoles.RIGHT:  // �Ҽ�ͷ
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
				System.out.print("ȷ���˳�������Ϸ��(y/n)");
				lock.unlock();
				int answer = 0;
				// �ȴ����밴��
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
	
	// ���·���ʹ�� Ranking �� Record �ദ���¼
	public void record() throws FileNotFoundException {
		Ranking ranking = new Ranking();
		String filename = "record.txt";
		ranking.loadRecord(filename);
		
		// �µļ�¼��Ĭ��û���ǳ�
		Record record = new Record(snake.range, snake.ateFoods, snake.level, "");
		// ��ʾ��ʷǰ����������
		showRank(ranking.get(record.getRange()), 3);
		
		// û��ʳ����˳�
		if (snake.ateFoods == 0) {
			return;
		}
		
		// ��ȡ��ǰ��¼��������
		int rank = ranking.checkRank(record);
		// ������ǰ�����ż�¼���ļ���
		if (rank <= 3) {
			getNickName(record, rank);       // ��ȡ�ǳ�
			ranking.addRecord(record);       // ���ӵ����а�
			ranking.deleteRecord(record.getRange(), 3);    // ֻ���� 3 ����¼
			ranking.saveRecord(filename);    // ���浽�ļ�
		}
	}
	
	// ��ʾǰ n ��
	public void showRank(TreeSet<Record> set, int n) {
		int rank = 0;            // ���Σ�Ĭ��û������		
		snake.gotoXY(snake.range + 2, 11);
		System.out.print("\t��ʷ������");
		snake.gotoXY(snake.range + 2, 12);
		System.out.print("\t����\t��Χ\tʳ��\t����\t����");
		if (set == null) {
			return;
		}
		Iterator<Record> it = set.iterator();
		if (n > set.size()) {   // ���� n ������ʵ��Ϊ׼
			n = set.size();
		}
		while (n > 0) {
			snake.gotoXY(snake.range + 2, 13 + rank);
			System.out.println("\t" + ++rank + "\t" + it.next());
			n--;
		}
	}
	
	// ��ȡ�û��ǳ�
	public void getNickName(Record record, int rank) {
		record.setNickName("����");         // Ĭ��Ϊ"����"
		snake.gotoXY(snake.range + 2, 16);
		System.out.println("\t���������ǣ�" + rank);
		snake.gotoXY(snake.range + 2, 17);
		System.out.print("\tҪ��¼������֣��ǳƣ���");
		Scanner input = new Scanner(System.in);
		char yn = input.next().charAt(0);
		if (yn == 'Y' || yn == 'y') {
			snake.gotoXY(snake.range + 2, 18);
			System.out.print("\t�������������֣��ǳƣ���");
			String nickName = input.next();
			record.setNickName(nickName);
		}
	}

}



