package com.example.ecommerce;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.plaf.synth.SynthStyleFactory;

public class Java8Features {
	
	public static void main(String[] args) {
		List<String> names = Arrays.asList(
		        "Ram",
		        "Krishna",
		        "Arjuna"
		);

		for(String name : names){
		    //System.out.println(name);
		}
		//Imperative Style
		//Tell Java HOW to do it
		//Streams
		List<String> names1 =
				Arrays.asList(
				    "Ram",
				    "Krishna",
				    "Arjuna",
				    "Shiva"
				);
		names1.stream().forEach(System.out::println);

		//filter name whose name length is greater than 5
		List<String> result = names1.stream().filter(num -> num.length()>5).toList();
		System.out.println(result);
		//filter name whose name length is greater than 5 and convert them into UPPERCASE
		List<String> result1 = names1.stream().filter(num -> num.length()>5).map(String::toUpperCase).toList();
		System.out.println(result1);
		List<Integer> numbers =
				Arrays.asList(
				    1,2,3,4,5,6,7,8,9,10,11
				);
		//Find the sum of all even numbers. expected output = 30
		int sum = numbers.stream().filter(num -> num%2 == 0).mapToInt(Integer::intValue).sum();
		System.out.println("sum of even numbers ::"+sum);
		
		//size of the list - we use count() method in streams
		System.out.println("Count :: "+numbers.stream().count());
		
		//find maximum integer in the list
		System.out.println("max interger from list :: "+numbers.stream().mapToInt(Integer::intValue).max().orElse(0)); // Returns 0 if the list was empty);
		//another alternative .max(Integer::compare) is used without converting it into primitive stream
		//It tells the stream: "Compare the numbers using the standard logic built into the Integer class, and give me the largest one."
		System.out.println("max interger from list :: "+numbers.stream().max(Integer::compareTo).orElse(0));
		//find first element in the list
		System.out.println("First element :: "+numbers.stream().findFirst());
		//Any matches found return boolean
		System.out.println("any macth ::"+numbers.stream().anyMatch(num -> num > 10));
		
		//1. Predicate 
		Predicate<Integer> isEven = num -> num % 2 == 0;
		System.out.println(isEven.test(10));
		if (isEven.test(12)) {
		    System.out.println(12 + " is even!");
		}
		//test call is not required while using in streams
		int sumOfEvenUsingPredicate = numbers.stream().filter(isEven).mapToInt(Integer::intValue).sum();
		 System.out.println(" sumOfEvenUsingPredicate :: "+sumOfEvenUsingPredicate);
	
	    //2. Function 
		 Function<String, Integer> nameLength = name -> name.length();
		 List<Integer> resultOfLenth = names1.stream().map(nameLength).toList();
		 System.out.println("name length is array ::"+resultOfLenth);
		 List<Integer> finalLengths1 = names1.stream()
				    .map(name -> name.length())          // 1. Convert Strings to Integers
				    .filter(length -> length > 5)        // 2. Filter the Integers directly (No .apply needed!)
				    .toList();
		 //use filter with names
		 System.out.println("finalLengths1 which are greater than 5 :"+ finalLengths1);
		 
		 //using function in filter 
		 List<String> grestestLenName = names1.stream().filter(name -> nameLength.apply(name) > 5).toList();
		 System.out.println("grestestLenName using function in filter ::"+grestestLenName);
		 //for me this is easier than above one :)
		names1.stream().filter(name -> name.length() > 5).toList();

		 //3. Consumer
		 Consumer<String> printer = name -> System.out.println("Hello, " + name);
		// You must call .accept() to run the logic
		printer.accept("Ram"); // Prints: Hello, Ram when do it manually, use accept 
		names.stream().forEach(printer); // in streams no need of accept, wow it prints the names :)
		//another way of printing namesSupplier<T>
		names.stream().forEach(System.out::println);//used method references here ..good :)

	}

}
