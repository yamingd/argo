package com.argo.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {
	
	public static List<?> timeDifference(Date date) throws Exception {
		List<Object> list = new ArrayList<Object>();
		if (null != date) {
			Date now = new Date();
			String str = "";
			long time;
			long diff = now.getTime() - date.getTime();
			long day = diff / (24 * 60 * 60 * 1000);
			long hour = (diff / (60 * 60 * 1000) - day * 24);
			long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (day > 0) {
				str = "天";
				time=day;
			} else if (hour > 0) {
				str = "小时";
				time=hour;
			} else if (min > 0) {
				str = "分钟";
				time=min;
			} else if (s > 0) {
				str = "秒";
				time=s;
			} else {
				str = "秒";
				time=1;
			}
			list.add(0, time);
			list.add(1, str);
		}
		return list;
	}
}
