package com.argo.core.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RteUtils {

	public static String clearXml(String text){
		String regex = "<!--\\[if gte .*?\\]>.*<!--\\[endif\\] --\\>"; //替换掉Word格式
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(text);
        if(m.find()){
        	text = text.replaceAll("}", "").replaceAll("\\{", "");
        	text = text.replaceAll(regex, "");
        }
        return text;
	}
	public static String clearStyle(String text){
		String regex = "\\s+(style\\s*=\\s*\".*?\")"; //替换掉Word格式
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(text);
        if(m.find()){
        	text = text.replaceAll("}", "").replaceAll("\\{", "");
        	text = text.replaceAll(regex, " ");
        }
        text = text.replaceAll("class=\"MsoNormal\"|lang=\"EN-US\"", "");
		return text;
	}
	public static String clear(String text){
		return clearXml(clearStyle(text));
	}
	public static String formatNR(String text){
		return text.replaceAll("\r\n", "<br />");
	}
	public static void main(String[] args){
		String t = "<p style=\"text-indent: 11pt;\" class=\"MsoNormal\"><span style=\"font-size: large; font-family: arial,helvetica,sans-serif;\"><span lang=\"EN-US\"><br></span></span></p>";
		System.out.println(clear(t));
	}
}
