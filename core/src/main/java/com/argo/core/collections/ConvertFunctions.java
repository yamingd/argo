package com.argo.core.collections;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Function;

/**
 * 将集合的元素转换方法
 * @author yaming_deng
 *
 */
public final class ConvertFunctions {

	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}

	public static Function<Object, Integer> toIntegerFunction() {
		return ToIntegerFunction.INSTANCE;
	}

	// enum singleton pattern
	private enum ToIntegerFunction implements Function<Object, Integer> {
		INSTANCE;
		public Integer apply(Object o) {
			checkNotNull(o);
			String str = ObjectUtils.toString(o);
			if(str.equals("")){
				throw new NullPointerException();
			}
			if(!StringUtils.isNumeric(str)){
				throw new java.lang.NumberFormatException("数字格式不对:"+str);
			}
			return Integer.parseInt(str);
		}

		@Override
		public String toString() {
			return "toString";
		}
	}
	
	public static Function<Object, Long> toLongFunction() {
		return ToLongFunction.INSTANCE;
	}

	// enum singleton pattern
	private enum ToLongFunction implements Function<Object, Long> {
		INSTANCE;
		public Long apply(Object o) {
			checkNotNull(o);
			String str = ObjectUtils.toString(o);
			if(str.equals("")){
				throw new NullPointerException();
			}
			if(!StringUtils.isNumeric(str)){
				throw new java.lang.NumberFormatException("数字格式不对:"+str);
			}
			return Long.parseLong(str);
		}

		@Override
		public String toString() {
			return "toString";
		}
	}
}
