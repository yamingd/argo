package com.argo.core.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 */
public class ClassUtils {

    public static <T> T getT(Class<?> clazz){

        // 得到具体的子类类型
        Type t = clazz.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            return  (T) p[0];
        }
        return null;
    }
}
