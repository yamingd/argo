package com.argo.tools.dbm;

import com.argo.core.annotation.EntityDef;
import com.argo.core.base.BaseEntity;

import java.util.List;

/**
 * Created by yaming_deng on 2014/10/11.
 */
@EntityDef(table = "tables")
public class Table extends BaseEntity {

    private String table_name;
    private String table_comment;

    private List<Column> columnList;

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_comment() {
        return table_comment;
    }

    public void setTable_comment(String table_comment) {
        this.table_comment = table_comment;
    }

    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    @Override
    public String getPK() {
        return null;
    }
}
