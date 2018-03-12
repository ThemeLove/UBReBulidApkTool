package com.umbrella.ubsdk.rebuild;

public class Test {
	public static void main(String[] args) {
		method1();
		System.out.println("aaaaaaaaaaa");
		System.out.println("bbbbbbbbbbb");
	}

	private static void method1() {
		
		System.out.println("ccccccccc");
		throw new RuntimeException(" test a error");
	}
}
