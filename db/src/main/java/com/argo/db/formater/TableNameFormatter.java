package com.argo.db.formater;

import java.util.Random;

/**
 * Created by yaming_deng on 14-8-28.
 */
public class TableNameFormatter {

    private int base;

    public TableNameFormatter(int base) {
        this.base = base;
    }

    public String get(String table, Long id){
        if(base == 0){
            return table;
        }
        int mod = id.intValue() % base;
        String tmp = base + "" + mod;
        int len = String.valueOf(base-1).length();
        tmp = tmp.substring(tmp.length() - len);
        table = String.format("%s_%s", table, tmp);
        return table;
    }

    /**
     * 十分表
     */
    public static TableNameFormatter T10 = new TableNameFormatter(10);
    /**
     * 百分表
     */
    public static TableNameFormatter T100 = new TableNameFormatter(100);
    /**
     * 千分表
     */
    public static TableNameFormatter T1000 = new TableNameFormatter(1000);

    public static void main(String[] args){
        long id = new Random().nextInt(Integer.MAX_VALUE);
        System.out.println(id);
        System.out.println(T10.get("user", id));
        System.out.println(T100.get("user", id));
        System.out.println(T1000.get("user", id));
    }
}
