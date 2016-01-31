package zj.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.*;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import zj.MyDate;
import zj.MyRunnable;

public class SimpleTest {

	@Test
	public void testEqualsHashCode() {
		int[] a= new int[2];
		int b;
		List al;
		
		
		HashMap<MyDate, Integer> hm = new HashMap<MyDate, Integer>();
		MyDate mc = new MyDate();

		hm.put(mc, 1);

		System.out.println("Value of HashMap is " + hm.get(mc));
		
		MyDate mc2 = new MyDate();
		
		if (mc.equals(mc2)){
			System.out.println("mc = mc2");
		}
		
		hm.put(mc2, 23);
		System.out.println("Value of HashMap is " + hm.get(mc));
		System.out.println("Value of HashMap2 is " + hm.get(mc2));
		System.out.println("Value of HashMap is " + hm.get(mc));
		
		int MAXIMUM_CAPACITY = 1 << 30;
		System.out.println(MAXIMUM_CAPACITY);
	}
	
	@Test
	public void testComparable() {
		MyDate d1 = new MyDate(1977,12,1);
		MyDate d2 = new MyDate(1977,12,3);
		MyDate d3 = new MyDate(1976,1,1);
		MyDate d4 = new MyDate(1347,12,1);
		
		MyDate[] a = new MyDate[]{d1,d2,d3,d4};
		
		Arrays.sort(a);
		
		System.out.println(Arrays.toString(a));
	}
	
	@Test
	public void testSerializable() {
		MyDate d1 = new MyDate(1977,12,1);
		MyDate d2 = new MyDate(1977,12,3);
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("a.txt"));
			oos.writeObject(d1);
			oos.writeObject(d2);
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("a.txt"));
			d1 = (MyDate)ois.readObject();
			d2 = (MyDate)ois.readObject();
			System.out.println(d1.toString());
			System.out.println(d2.toString());
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCloneable() {
		MyDate d1 = new MyDate(1999,1,1, null);
		MyDate d2 = new MyDate(1999,1,2, d1);
		
		try {
			MyDate d3 = (MyDate) d2.clone();
			MyDate d4 = d3.next;
			assertTrue(d3!=d2);
			assertTrue(d4.equals(d1));
			assertTrue(d4==d1);
			System.out.println(d3.toString());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeepClone() {
		MyDate d1 = new MyDate(1999,1,1, null);
		MyDate d2 = new MyDate(1999,1,2, d1);
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("a.txt"));
			oos.writeObject(d2);
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("a.txt"));
			MyDate d3 = (MyDate)ois.readObject();
			ois.close();

			System.out.println("before de-se:" + d2);
			System.out.println("after de-se:" + d3);
			MyDate d4 = d3.next;
			assertTrue(d3!=d2);
			assertTrue(d4.equals(d1));
			assertTrue(d4!=d1);
			System.out.println(d3.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testIterator() {
		ArrayList<String> als = new ArrayList<String>();
		als.add("1");
		als.add("2");
		
		Iterator<String> is = als.iterator();
		Iterator<String> is2 = als.iterator();
		
		while (is.hasNext()){
			String s = is.next();
		}
	}

}
