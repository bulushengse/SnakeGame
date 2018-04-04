package com.bwf.play;

import java.util.*;
import com.bwf.game.*;
import com.bwf.jni.*;

public class PlaySnakeGame {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		do {
			Consoles.setScreenColor(0xf, 0x0);	// �׵׺���
			Consoles.clearScreen();		// ����
			// �û�����ǽ��С
			int wallSize = 0;
			while (true) {
				try {
					System.out.println("������ǽ�Ĵ�С����15 - 30��");
					wallSize = input.nextInt();
					if (wallSize < 15 || wallSize > 30) {
						System.out.println("ǽ�Ĵ�С��Χ�����ǣ�15 - 30��");
						continue;
					}
					break;
				} catch (InputMismatchException ex) {
					System.out.println("������������");
					input.nextLine();
				}
			}
			Consoles.clearScreen();	// ����
			// �ṩ�ߵĳ�ʼλ�ã���λ����ָ����ǽ�ڵĻ��Χ
			// ���б�ʾ�߳����Ľڣ�ÿ�е� 0 �б�ʾ���Χ�����±꣬
			// �� 1 �б�ʾ���Χ�����±꣬�����±�� 0 ��ʼ��
			int[][] pos = {{3, 2}, {2, 2}, {1, 2}, {0, 2}};
			// ����̰������Ϸ����
			SnakeGame game = new SnakeGame(wallSize, pos, Snake.DIR_RIGHT);
			// ��ʼ̰������Ϸ
			game.start();

			// ��ʾ����
			Consoles.gotoXY(Wall.START_X + 4, Wall.START_Y + wallSize + 5);
			System.out.print("����һ����(y/n)");
			String yn = input.next();
			if (yn.charAt(0) == 'Y' || yn.charAt(0) == 'y') {
				continue;
			}
			System.out.println("��ӭ�´����棡");
			break;
		} while (true);
		Consoles.clearScreen();	// ����
		Consoles.showCursor();	// ��ʾ���
		input.close();
	}

}