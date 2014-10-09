package com.argo.core.json;

import com.argo.core.ContextConfig;
import com.argo.core.exception.JsonDSException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * gson的工具类
 *
 */
public class JsonUtil {

    protected Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    protected static Gson gson = null;
    protected static MessagePack messagePack;

    static {
        messagePack = new MessagePack();
        //创建GsonBuilder
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Object.class, new NaturalDeserializer());

        //设置时间格式
        builder.setDateFormat(ContextConfig.DATE_TIME_FORMAT);
        gson = builder.create();
    }

    /**
     * 转换JSON为List<T>
     * @param json
     * @param itemType
     * @param <T>
     * @return
     * @throws JsonDSException
     */
    public static <T> List<T> asList(String json, final T[] itemType)throws Exception {
        T[] temp = (T[])gson.fromJson(json, itemType.getClass());
        return Arrays.asList(temp);
    }

    /**
     * 转换二进制JSON为List<T>
     * @param json
     * @param itemType
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> asList(byte[] json, final T[] itemType)throws Exception {
        T[] temp = (T[])messagePack.read(json, itemType.getClass());
        return Arrays.asList(temp);
    }
    /**
     *
     * @param clazz
     * @param json
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T asT(Class<?> clazz, String json) {
        if (json == null) return null;
        return (T) gson.fromJson(json, clazz);
    }

    /**
     * 从二进制JSON转为对象
     * @param clazz
     * @param json
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T asT(Class<?> clazz, byte[] json) throws IOException {
        if (json == null || json.length == 0)
            return null;
        return (T) messagePack.read(json, clazz);
    }

    /**
     * 将一个对象转成json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 转为二进制JSON.
     * @param obj
     * @return
     * @throws Exception
     */
    public static <T> byte[] toBytes(T obj) throws Exception {
        if (obj == null){
            throw new IllegalArgumentException("obj is NULL.");
        }
        return messagePack.write(obj);
    }

    /**
     *
     * @param json
     * @return
     */
    public static Map<String, Object> asMap(String json) {
        if (json == null) return null;
        return gson.fromJson(json, HashMap.class);
    }

    public static HashMap asMap(byte[] json) throws IOException {
        if (json == null)
            return null;
        return messagePack.read(json, HashMap.class);
    }
}
