package com.argo.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateRangeUtil {

	/**
	 * 获取上个月日期范围。包括起止
	 * @return
	 */
	public static Date[] getLastMonth(){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, -1);
		calendar.set(GregorianCalendar.DATE, 1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		Date begin = calendar.getTime();
		
		calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.DATE, 1);
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		
		Date end = calendar.getTime();
		
		return new Date[]{begin,end};
	}
	
	/**
	 * 返回这个月日期范围。包括起止
	 * @return
	 */
	public static Date[] getThisMonth(){
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		calendar.set(GregorianCalendar.DATE, 1);
		Date begin = calendar.getTime();
		
		calendar.add(GregorianCalendar.MONTH, 1);
		calendar.add(GregorianCalendar.DATE, -1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		
		Date end = calendar.getTime();
		
		return new Date[]{begin,end};
	}
	
	/**
	 * 获取上周日期范围。包括起止
	 * @return
	 */
	public static Date[] getLastWeek(){
		/*
		 * 一周为：星期日到星期六
		 * 星期日(1),星期一(2),...,星期六(7)
		 * */
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK);
		
		calendar.add(GregorianCalendar.DAY_OF_WEEK, -minus);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		Date end = calendar.getTime();
		
		calendar.add(GregorianCalendar.DAY_OF_WEEK, -6);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		Date begin = calendar.getTime();
		
		return new Date[]{begin,end};
	}
	
	public static Integer[] getLastWeekYW(){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK);
		
		calendar.add(GregorianCalendar.DAY_OF_WEEK, -minus);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		
		return new Integer[]{calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.WEEK_OF_YEAR)};
	}
	
	public static Integer[] getThisWeekYW(){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		return new Integer[]{calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.WEEK_OF_YEAR)};
	}
	
	/**
	 * 获取这周日期范围。包括起止
	 * @return
	 */
	public static Date[] getThisWeek(){
		/*
		 * 一周为：星期日到星期六
		 * 星期日(1),星期一(2),...,星期六(7)
		 * */
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK);
		
		calendar.add(GregorianCalendar.DAY_OF_WEEK, -minus+1);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		Date begin = calendar.getTime();
		
		calendar.add(GregorianCalendar.DAY_OF_WEEK, 6);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		Date end = calendar.getTime();
		
		return new Date[]{begin,end};
	}
	
	/**
	 * 获取前n天
	 * @param days
	 * @return
	 */
	public static Date getLastBefore(Integer days){
		GregorianCalendar calendar = new GregorianCalendar();	
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		
		calendar.add(GregorianCalendar.DATE, -4);
		Date begin = calendar.getTime();
		
		return begin;
	}
	
	public static void main(String [] arges){
		Date[] ranges = getLastMonth();
		System.out.println("LastMonth");
		System.out.println(ranges[0]+"/"+DateFormatUtils.format(ranges[0],"yyyy-MM-dd HH:mm:ss"));
		System.out.println(ranges[1]+"/"+DateFormatUtils.format(ranges[1],"yyyy-MM-dd HH:mm:ss"));
		
		ranges = getLastWeek();
		System.out.println("LastWeek");
		System.out.println(ranges[0]);
		System.out.println(ranges[1]);
		
		ranges = getThisMonth();
		System.out.println("ThisMonth");
		System.out.println(ranges[0]);
		System.out.println(ranges[1]);
		
		ranges = getThisWeek();
		System.out.println("ThisWeek");
		System.out.println(ranges[0]);
		System.out.println(ranges[1]);
		
		System.out.println("LastBefore:4");
		System.out.println(getLastBefore(4));
	}
}