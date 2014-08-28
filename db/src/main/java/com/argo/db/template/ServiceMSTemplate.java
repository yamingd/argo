package com.argo.db.template;

import com.argo.core.base.BaseBean;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.db.JdbcConfig;
import com.argo.db.MasterSlaveJdbcTemplate;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-28.
 */
public abstract class ServiceMSTemplate<T> extends BaseBean implements ServiceBase<T> {

    protected JdbcTemplate jdbcTemplateM;
    protected JdbcTemplate jdbcTemplateS;

    @Autowired
    private MasterSlaveJdbcTemplate masterSlaveJdbcTemplate;

    private JdbcConfig jdbcConfig;

    //protected Class<T> ROW_CLASS;
    //protected RowMapper<T> ROW_MAPPER;

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcConfig = JdbcConfig.current;
        Map<String, String> sm = jdbcConfig.get(Map.class, "ems");
        String name = sm.get(this.getServerName());
        jdbcTemplateM = masterSlaveJdbcTemplate.get(name, true);
        jdbcTemplateS = masterSlaveJdbcTemplate.get(name, false);

        //this.ROW_CLASS = (Class<T>) ClassUtils.getTypeArguments(ServiceMSTemplate.class, getClass()).get(0);
        //this.ROW_MAPPER = new BeanPropertyRowMapper<T>(this.ROW_CLASS);

        Assert.notNull(jdbcTemplateM, "jdbcTemplateM is NULL.");
        Assert.notNull(jdbcTemplateS, "jdbcTemplateS is NULl");
    }

    protected void update(String tableName, Map<String, Object> args, String pk, Object pkvalue){
        List<Object> params = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" set ");
        for (String s : args.keySet()) {
            sb.append(s).append("= ? ,");
            params.add(args.get(s));
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("  where ").append(pk).append(" = ? ");
        params.add(pkvalue);

        this.jdbcTemplateM.update(sb.toString().intern(), params.toArray());
    }

    protected abstract String getServerName();

    /**
     * 读取详情
     * @param oid
     * @return
     * @throws ServiceException
     */
    @Override
    public T findById(Long oid)throws EntityNotFoundException {
        return null;
    }

    /**
     * 添加记录
     * @param entity
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean add(T entity) throws ServiceException{
        return false;
    }

    /**
     * 更新记录
     * @param entity
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean update(T entity) throws ServiceException{
        return false;
    }

    /**
     * 移除记录.
     * @param clazz
     * @param oid
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean remove(Class<T> clazz, Long oid) throws ServiceException{
        return false;
    }
}
