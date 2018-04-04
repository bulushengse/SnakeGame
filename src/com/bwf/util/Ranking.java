package com.bwf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// ���а��࣬���������ߵĻ��Χ����ֵ���� TreeSet<Record>���������¼�����μ�
public class Ranking extends TreeMap<Integer, TreeSet<Record>> {
	// ע���ɼ���ͬ��ͬ���߲���¼
	
	// �����ļ��еļ�¼
	public void loadRecord(String filename) throws FileNotFoundException {
		File file = new File(filename);
		// �ļ����ھͶ�ȡ�����ݵ� TreeSet ��
		if (file.exists()) {
			Scanner input = new Scanner(new FileInputStream(file));
			TreeSet<Record> set = new TreeSet<Record>();
			Record previousRecord = null;
			Record nextRecord = null;
			// ��һ����¼��������
			if (input.hasNext()) {
				previousRecord = nextRecord = new Record(input.nextInt(), input.nextInt(), 
						input.nextInt(), input.next());
				set.add(nextRecord);
			}
			// ����ʣ���¼
			while (input.hasNext()) {
				nextRecord = new Record(input.nextInt(), input.nextInt(), 
						input.nextInt(), input.next());
				if (previousRecord.getRange() == nextRecord.getRange()) {
					set.add(nextRecord);
				} else {  // ��Χ��ͬ�򱣴浽�µ� Set ��
					put(previousRecord.getRange(), set);
					set = new TreeSet<Record>();
					set.add(nextRecord);
				}
				previousRecord = nextRecord;
			}
			// ���һ�� Set ��ӵ� Map
			if (!set.isEmpty()) {
				// ��ӵ� Map
				put(previousRecord.getRange(), set);
			}
			input.close();
		}
	}
	
	// �����¼���ļ�
	public void saveRecord(String filename) throws FileNotFoundException {
		File file = new File(filename);
		PrintWriter output = new PrintWriter(new FileOutputStream(file));
		Set<Integer> set = keySet();                    // ��ȡ�� Set
		Iterator<Integer> it = set.iterator();          // ������ Set �Եõ���Ӧ Set
		while (it.hasNext()) {
			int range = it.next();
			TreeSet<Record> treeSet = get(range);   // ��ȡ��¼ Set
			while (!treeSet.isEmpty()) {
				output.println(treeSet.pollFirst().toString());  // ��¼д���ļ�
			}
		}
		output.flush();  // ˢ���ļ�
		output.close();  // �ر��ļ�
	}
	
	// Ԥ�Ƽ�¼������
	public int checkRank(Record record) {
		TreeSet<Record> set = get(record.getRange());
		if (set == null) {   // û�����ַ�Χ�ļ�¼����Ĭ�������� 1
			return 1;
		}
		Iterator<Record> it = set.iterator();
		int rank = 1;
		while (it.hasNext()) {
			if (record.compareTo(it.next()) < 0) {
				return rank;
			}
			rank++;
		}
		return rank;
	}
	
	// ������¼
	public void addRecord(Record record) {
		TreeSet<Record> set = get(record.getRange());
		if (set == null) {   // û�����ַ�Χ�ļ�¼�����½� Set
			set = new TreeSet<Record>();
		}
		set.add(record);     // ���Ӽ�¼
		put(record.getRange(), set);
	}
	
	// ����Ϊ key �� Set ɾ����¼����ָ������ n
	public void deleteRecord(int key, int n) {
		TreeSet<Record> set = get(key);
		if (set != null) {
			while (set.size() > n) {   // ���� n ����¼
				set.pollLast();
			}
		}
	}
}
