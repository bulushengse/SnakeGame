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

// 排行榜类，“键”是蛇的活动范围，“值”是 TreeSet<Record>，即保存记录的树形集
public class Ranking extends TreeMap<Integer, TreeSet<Record>> {
	// 注：成绩相同的同名者不记录
	
	// 加载文件中的记录
	public void loadRecord(String filename) throws FileNotFoundException {
		File file = new File(filename);
		// 文件存在就读取其内容到 TreeSet 中
		if (file.exists()) {
			Scanner input = new Scanner(new FileInputStream(file));
			TreeSet<Record> set = new TreeSet<Record>();
			Record previousRecord = null;
			Record nextRecord = null;
			// 第一条记录单独处理
			if (input.hasNext()) {
				previousRecord = nextRecord = new Record(input.nextInt(), input.nextInt(), 
						input.nextInt(), input.next());
				set.add(nextRecord);
			}
			// 处理剩余记录
			while (input.hasNext()) {
				nextRecord = new Record(input.nextInt(), input.nextInt(), 
						input.nextInt(), input.next());
				if (previousRecord.getRange() == nextRecord.getRange()) {
					set.add(nextRecord);
				} else {  // 范围不同则保存到新的 Set 中
					put(previousRecord.getRange(), set);
					set = new TreeSet<Record>();
					set.add(nextRecord);
				}
				previousRecord = nextRecord;
			}
			// 最后一个 Set 添加到 Map
			if (!set.isEmpty()) {
				// 添加到 Map
				put(previousRecord.getRange(), set);
			}
			input.close();
		}
	}
	
	// 保存记录到文件
	public void saveRecord(String filename) throws FileNotFoundException {
		File file = new File(filename);
		PrintWriter output = new PrintWriter(new FileOutputStream(file));
		Set<Integer> set = keySet();                    // 获取键 Set
		Iterator<Integer> it = set.iterator();          // 迭代键 Set 以得到对应 Set
		while (it.hasNext()) {
			int range = it.next();
			TreeSet<Record> treeSet = get(range);   // 获取记录 Set
			while (!treeSet.isEmpty()) {
				output.println(treeSet.pollFirst().toString());  // 记录写入文件
			}
		}
		output.flush();  // 刷新文件
		output.close();  // 关闭文件
	}
	
	// 预计记录的排名
	public int checkRank(Record record) {
		TreeSet<Record> set = get(record.getRange());
		if (set == null) {   // 没有这种范围的记录，则默认排名第 1
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
	
	// 新增记录
	public void addRecord(Record record) {
		TreeSet<Record> set = get(record.getRange());
		if (set == null) {   // 没有这种范围的记录，则新建 Set
			set = new TreeSet<Record>();
		}
		set.add(record);     // 增加记录
		put(record.getRange(), set);
	}
	
	// 将键为 key 的 Set 删除记录数到指定条数 n
	public void deleteRecord(int key, int n) {
		TreeSet<Record> set = get(key);
		if (set != null) {
			while (set.size() > n) {   // 保留 n 条记录
				set.pollLast();
			}
		}
	}
}
