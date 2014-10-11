package com.argo.tools.dbm;

import com.argo.core.annotation.Model;
import com.argo.db.template.ServiceMSTemplate;
import com.argo.service.annotation.RmiService;
import net.sf.jxls.transformer.XLSTransformer;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 2014/10/11.
 */
@Model(Table.class)
@RmiService(serviceInterface = TableService.class)
public class TableServiceImpl extends ServiceMSTemplate<Table> implements TableService {

    public static RowMapper<Table> Table_Mapper = new BeanPropertyRowMapper<Table>(Table.class);

    public static RowMapper<Column> Column_Mapper = new BeanPropertyRowMapper<Column>(Column.class);

    @Override
    public List<Table> findAll(String schemaName) {
        String sql = "SELECT table_name, table_comment FROM INFORMATION_SCHEMA.tables t WHERE table_schema=?";
        return this.jdbcTemplateS.query(sql, Table_Mapper, schemaName);
    }

    @Override
    public List<Column> findAllColumns(String schemaName, String tableName) {
        String sql = "select table_name,column_name,column_comment,column_type,column_key,data_type from INFORMATION_SCHEMA.COLUMNS where table_schema = ? and table_name = ?";
        return this.jdbcTemplateS.query(sql, Column_Mapper, schemaName, tableName);
    }

    @Override
    public void exportXls(String schemaName, String mode) throws Exception {
        String templateFileName = String.format("table-schema-%s.xls", mode);
        String destFileName = String.format("%s-%s.xls",schemaName, mode);

        List<Table> tableList = this.findAll(schemaName);
        for (Table table : tableList){
            List<Column> columns = this.findAllColumns(schemaName, table.getTable_name());
            table.setColumnList(columns);
        }

        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName);

        Map beans = new HashMap();
        beans.put("tables", tableList);
        XLSTransformer transformer = new XLSTransformer();
        org.apache.poi.ss.usermodel.Workbook workbook =  transformer.transformXLS(is, beans);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(destFileName));
        workbook.write(os);
        is.close();
        os.flush();
        os.close();
    }
}
