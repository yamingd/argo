package com.argo.db.template;

import com.argo.core.base.BaseBean;
import com.argo.core.base.BaseEntity;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.core.utils.ClassUtils;
import com.argo.db.JdbcConfig;
import com.argo.db.MasterSlaveJdbcTemplate;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by yaming_deng on 14-8-28.
 */
public abstract class ServiceMSTemplate<T extends BaseEntity> extends BaseBean {

    public static final String SQL_FIND_BYID = "select * from %s where %s = ?";
    protected JdbcTemplate jdbcTemplateM;
    protected JdbcTemplate jdbcTemplateS;

    @Autowired
    private MasterSlaveJdbcTemplate masterSlaveJdbcTemplate;

    private JdbcConfig jdbcConfig;

    protected Class<T> entityClass;
    protected RowMapper<T> entityMapper;
    protected EntityTemplate entityTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        jdbcConfig = JdbcConfig.current;
        Map<String, String> sm = jdbcConfig.get(Map.class, "ems");
        String name = sm.get(this.getServerName());
        if (StringUtils.isBlank(name)){
            name = this.getServerName();
        }

        jdbcTemplateM = masterSlaveJdbcTemplate.get(name, true);
        jdbcTemplateS = masterSlaveJdbcTemplate.get(name, false);

        this.entityClass = (Class<T>) ClassUtils.getTypeArguments(ServiceMSTemplate.class, getClass()).get(0);
        this.entityTemplate = new EntityTemplate<T>(this.entityClass);
        this.entityMapper = new BeanPropertyRowMapper<T>(this.entityClass);

        Assert.notNull(jdbcTemplateM, "jdbcTemplateM is NULL.");
        Assert.notNull(jdbcTemplateS, "jdbcTemplateS is NULl");
    }

    protected int update(String tableName, Map<String, Object> args, String pk, Object pkvalue){
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

        return this.jdbcTemplateM.update(sb.toString().intern(), params.toArray());
    }

    protected abstract String getServerName();

    /**
     * 读取详情
     * @param oid
     * @return
     * @throws ServiceException
     */
    protected T findEntityById(Long oid)throws EntityNotFoundException {
        final String sql = String.format(SQL_FIND_BYID, this.entityTemplate.getTable(), this.entityTemplate.getPk());
        List<T> list = this.jdbcTemplateS.query(sql, this.entityMapper, oid);
        if (list.size() == 0){
            throw new EntityNotFoundException(this.entityTemplate.getTable(), "findById", "id not found", oid);
        }
        return list.get(0);
    }

    /**
     * 添加记录
     * @param entity
     * @return 主键
     * @throws ServiceException
     */
    protected Long addEntity(T entity) throws ServiceException{

        StringBuffer sql = new StringBuffer("insert into ").append(this.entityTemplate.getTable()).append(" (");
        final List<Object> args = new ArrayList<Object>();
        List<String> qm = new ArrayList<String>();

        Collection<Field> fields = this.entityTemplate.getFields();
        for (Field field : fields){
            try {
                Object v = field.get(entity);
                if (v == null){
                    continue;
                }
                args.add(v);
                qm.add("?");
                sql.append(field.getName());
                sql.append(", ");
            } catch (IllegalAccessException e) {
                throw new ServiceException(e.getMessage(), e);
            }
        }

        if (args.size() == 0){
            throw new ServiceException("entity has no value. all field's value is NULL");
        }

        sql.setLength(sql.length() - 2);
        sql.append(") values (").append(StringUtils.join(qm, ", ")).append(")");

        final String sqlstr = sql.toString().intern();

        if (logger.isDebugEnabled()){
            logger.info(sqlstr);
        }

        if (this.entityTemplate.isMultiPK()){
            long ret = this.jdbcTemplateM.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                        Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sqlstr,
                            Statement.RETURN_GENERATED_KEYS);

                    for (int i = 0; i < args.size(); i++) {
                        ps.setObject(i + 1, args.get(i));
                    }

                    return ps;
                }
            });

            return ret;

        }else{

            KeyHolder holder = new GeneratedKeyHolder();
            this.jdbcTemplateM.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(
                        Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sqlstr,
                            Statement.RETURN_GENERATED_KEYS);

                    for (int i = 0; i < args.size(); i++) {
                        ps.setObject(i + 1, args.get(i));
                    }

                    return ps;
                }
            }, holder);

            return holder.getKey().longValue();
        }

    }

    /**
     * 更新记录
     * @param entity
     * @return
     * @throws ServiceException
     */
    protected boolean updateEntity(T entity) throws ServiceException{
        return false;
    }


    /**
     * 移除记录.
     * @param oid
     * @return
     * @throws ServiceException
     */
    protected boolean removeEntity(Long oid) throws ServiceException{
        String sql;
        int count = 0;
        if (this.entityTemplate.isHasIfDeleted()){
            sql = String.format("update %s set ifDeleted=?, deleteAt=? where %s = ?", this.entityTemplate.getTable(), this.entityTemplate.getPk());
            count = this.jdbcTemplateM.update(sql, 1, new Date(), oid);
        }else{
            sql = String.format("delete from %s where %s = ?", this.entityTemplate.getTable(), this.entityTemplate.getPk());
            count = this.jdbcTemplateM.update(sql, oid);
        }
        return count > 0;
    }
}
