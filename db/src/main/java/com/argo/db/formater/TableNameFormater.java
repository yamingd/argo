package com.argo.db.formater;

/**
 * Created by yaming_deng on 14-8-28.
 */
public class TableNameFormater {

    private int base;

    public TableNameFormater(int base) {
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
     * 百分表
     */
    public static TableNameFormater T100 = new TableNameFormater(100);
    /**
     * 千分表
     */
    public static TableNameFormater T1000 = new TableNameFormater(1000);

}
