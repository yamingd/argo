package com.argo.tools.dbm;

import com.argo.db.template.ServiceBase;

import java.util.List;

/**
 * Created by Administrator on 2014/10/11.
 */
public interface TableService extends ServiceBase<Table> {

    /**
     *
     * @param schemaName
     * @return
     */
    List<Table> findAll(String schemaName);

    /**
     *
     * @param schemaName
     * @param tableName
     * @return
     */
    List<Column> findAllColumns(String schemaName, String tableName);

    void exportXls(String schemaName, String mode) throws Exception;
}
