package com.argo.db.template;

import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yaming_deng on 2014/10/8.
 */
public class SimpleColumnMapRowMapper extends ColumnMapRowMapper {

    @Override
    protected Map<String, Object> createColumnMap(int columnCount) {
        return new HashMap<String, Object>();
    }
}
