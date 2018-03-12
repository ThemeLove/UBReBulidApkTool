package com.umbrella.ubsdk.rebuild;

public class TextUtil {

	public static boolean isEmpty(String str){
		return str==null?true:str.length()<=0;
	}
	
	public static boolean equals(String str1,String str2){
		if (str1==null||str2==null) return false;
		return str1.equals(str2);
	}
	
	public static boolean equalsIgnoreCase(String str1,String str2){
		if (str1==null||str2==null) return false;
		return str1.equals(str2);
	}
}
