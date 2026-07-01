package com.example.ecommerce;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.context.properties.bind.DefaultValue;

public class TestJavaFeatures {

	public static void main(String[] args) {
		//findMissingNumberFromList();
		int arr[] = {2,4,2,3,4,5,5,7,8,3,5,7,8,1,10};
		Map<Integer, Integer> result = new TreeMap();
		for (int i : arr) {
			result.put(i, result.getOrDefault(i, 0)+1);
		}
		String name = "Hello ";
		name = name.concat("World");
		System.out.println("name :::"+name);
		
		Thread A = new Counter();
		Thread B = new Counter();
		Counter counter = new Counter();
		A.run();;
		B.run();;
		/*
		 * for(int i=0;i<1000;i++){
		 * System.out.println("current thread ::"+Thread.currentThread().getName());
		 * counter.increment(); }
		 *///The output is unpredictable and may be less than 2000 because volatile guarantees visibility but does not make count++ atomic. Multiple threads can overwrite each other's updates causing race conditions.
		counter.print();
	}

	private static void findMissingNumberFromList() {
		int arr[] = {2,4,3,5,7,8,1,10};
		int givenArraySize = Arrays.stream(arr).sum();
		int actualSize = IntStream.rangeClosed(0, 9).sum();
		System.out.println("Missing number is "+(actualSize-givenArraySize));
		//if only one number is missing then this is the best approach to substract the size
		//but if more than one number is missing then we need another approch 
		//if there are duplicates in list then we need to convert it to set other wise convert to toList()
		Set<Integer> set = Arrays.stream(arr).boxed().collect(Collectors.toSet());
		OptionalInt max = Arrays.stream(arr).max();
		System.out.println("max from array :: "+Arrays.stream(arr).max());
		List<Integer> missing = IntStream.range(1, max.getAsInt()+1).filter(i-> !set.contains(i)).boxed().collect(Collectors.toList());
		
		System.out.println("missing :: "+missing);
	}
}
class Counter extends Thread{

    volatile int count = 0;

    public void increment() {
        count++;
    }
    @Override
    public void run() {
    	super.run();
    	increment();
		System.out.println("current thread ::"+Thread.currentThread().getName());

    }
    public void print() {
		System.out.println("Count when using volatile ::"+count);
    }
    
    
}
