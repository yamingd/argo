package com.argo.db.template;

import com.argo.core.annotation.Model;
import com.argo.core.base.BaseBean;
import com.argo.core.base.BaseEntity;
import com.argo.core.base.CacheBucket;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.db.JdbcConfig;
import com.argo.db.MasterSlaveJdbcTemplate;
import com.argo.db.ServiceInstanceFactory;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yaming_deng on 14-8-28.
 */
public abstract class ServiceMSTemplate extends BaseBean implements ServiceBase {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String SQL_FIND_BYID = "select * from %s where %s = ?";
    public static final String SQL_FIND_BYIDS = "select * from %s where %s";

    protected static ExecutorService executor = Executors.newCachedThreadPool();

    protected JdbcTemplate jdbcTemplateM;
    protected JdbcTemplate jdbcTemplateS;

    @Autowired
    private MasterSlaveJdbcTemplate masterSlaveJdbcTemplate;

    private JdbcConfig jdbcConfig;

    protected RowMapper<?> entityMapper;
    protected EntityTemplate entityTemplate;
    protected Class<?> entityClass;

    @Autowired(required=false)
    protected CacheBucket cacheBucket;

    @Autowired
    protected ServiceInstanceFactory serviceInstanceFactory;

    public ServiceMSTemplate() {
        Model annotation = this.getClass().getAnnotation(Model.class);
        if (annotation == null){
            return;
        }

        this.entityClass = annotation == null ? null : annotation.value();
        this.entityTemplate = new EntityTemplate(entityClass);
        this.entityMapper = new BeanPropertyRowMapper(entityClass);
    }

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

        Assert.notNull(jdbcTemplateM, "jdbcTemplateM is NULL.");
        Assert.notNull(jdbcTemplateS, "jdbcTemplateS is NULl");

        if (this.cacheBucket == null){
            logger.warn("CacheBucket Not Found. Cache is disabled.");
        }else{
            logger.info("CacheBucket Found. Cache is enabled.");
        }

        if (this.entityClass != null) {
            serviceInstanceFactory.add(this.entityClass, this);
        }
    }

    protected void disableCache(){
        this.cacheBucket = null;
    }

    protected int update(Object pkvalue, Map<String, Object> args){
        List<Object> params = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(this.entityTemplate.getTable()).append(" set ");
        for (String s : args.keySet()) {
            sb.append(s).append("= ? ,");
            params.add(args.get(s));
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("  where ").append(this.entityTemplate.getPk()).append(" = ? ");
        params.add(pkvalue);

        int count = this.jdbcTemplateM.update(sb.toString().intern(), params.toArray());

        if (this.cacheBucket != null){
            String key = genCacheKey(":"+pkvalue);
            this.cacheBucket.remove(key);
        }

        return count;
    }

    protected String getServerName(){
        return this.entityTemplate.getTable();
    }

    /**
     * 读取详情
     * @param oid
     * @return
     * @throws ServiceException
     */
    @Override
    public <T> T findById(Long oid)throws EntityNotFoundException {
        if (oid == null){
            throw new EntityNotFoundException(this.entityTemplate.getTable(), "findById", "id not found", oid);
        }
        String key = genCacheKey(":"+oid);
        if (this.cacheBucket != null && this.entityClass != null){
            Object o = this.cacheBucket.geto(this.entityClass, key);
            if (o != null){
                return (T)o;
            }
        }
        final String sql = String.format(SQL_FIND_BYID, this.entityTemplate.getTable(), this.entityTemplate.getPk());
        return getT(this.jdbcTemplateS, oid, key, sql);
    }

    protected <T> T getT(JdbcTemplate jdbcTemplate, Long id) throws EntityNotFoundException {
        String key = genCacheKey(":"+id);
        final String sql = String.format(SQL_FIND_BYID, this.entityTemplate.getTable(), this.entityTemplate.getPk());
        return this.getT(jdbcTemplate, id, key, sql);
    }

    protected <T> T getT(JdbcTemplate jdbcTemplate, Long oid, String key, String sql) throws EntityNotFoundException {
        List<T> list = (List<T>) jdbcTemplate.query(sql, this.entityMapper, oid);
        if (list.size() == 0){
            throw new EntityNotFoundException(this.entityTemplate.getTable(), "findById", "id not found", oid);
        }
        T o = list.get(0);
        if (o != null && this.cacheBucket != null && this.entityClass != null){
            this.cacheBucket.put(key, o, getEntityTTL());
        }
        return o;
    }

    protected int getEntityTTL(){
        return 3600 * 8;
    }

    protected String genCacheKey(Object oid) {
        return String.format("%s%s", this.entityTemplate.getTable(), oid);
    }

    @Override
    public <T> List<T> findByIds(List<Long> oids) {
        return this.findByIds(oids, true);
    }

    @Override
    public <T> List<T> findByIds(List<Long> itemIds, boolean ascending){
        if (itemIds == null || itemIds.size() == 0){
            return Lists.newArrayList();
        }

        if (this.cacheBucket != null && this.entityClass != null){
            List<String> keys = Lists.newArrayList();
            for (Long id : itemIds){
                keys.add(genCacheKey(":"+id));
            }
            List items = this.cacheBucket.geto(this.entityClass, keys.toArray(new String[0]));
            if (null != items && items.size() > 0) {
                List<Long> nullIds = Lists.newArrayList();
                for (int i = 0; i < itemIds.size(); i++) {
                    Object o = items.get(i);
                    if (o == null) {
                        nullIds.add(itemIds.get(i));
                        items.set(i, null);
                    } else {
                        items.set(i, (T) o);
                    }
                }

                if (nullIds.size() > 0 ){
                    logger.info("nullIds: {}", nullIds);
                    List<T> listFromDb = this.loadFromDb(this.jdbcTemplateS, nullIds, ascending);
                    if (listFromDb.size() > 0) {
                        logger.info("listFromDb total={}", listFromDb.size());
                        int i = 0, j = 0;
                        for (; i < items.size(); i++) {
                            if (items.get(i) == null) {
                                T o = listFromDb.get(j);
                                items.set(i, o);
                                j++;
                            }
                        }
                    }else{
                        logger.error("Records Not Found By Ids: {}", nullIds);
                    }
                }

                return items;
            }
        }

        return this.loadFromDb(this.jdbcTemplateS, itemIds, ascending);

    }

    protected  <T> List<T> loadFromDb(JdbcTemplate jdbcTemplate, List<Long> itemIds, boolean ascending){
        StringBuilder s = new StringBuilder(100);
        for (int i = 0; i < itemIds.size(); i++) {
            s.append(String.format(" %s = ? ", this.entityTemplate.getPk()));
            s.append(" OR ");
        }
        s.setLength(s.length() - 4);
        if (ascending){
            s.append(String.format(" order by %s", this.entityTemplate.getPk()));
        }else{
            s.append(String.format(" order by %s desc", this.entityTemplate.getPk()));
        }

        final String sql = String.format(SQL_FIND_BYIDS, this.entityTemplate.getTable(), s.toString());
        final List<T> list = (List<T>) jdbcTemplate.query(sql, itemIds.toArray(new Long[0]), this.entityMapper);

        if (list.size() > 0) {

            if (this.cacheBucket != null && this.entityClass != null){
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (T item : list) {
                            String key = genCacheKey(((BaseEntity)item).getPK());
                            try {
                                cacheBucket.put(key, item, getEntityTTL());
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }
                });
            }
        }

        return list;
    }

    /**
     * 添加记录
     * @param entity
     * @return 主键
     * @throws ServiceException
     */
    @Override
    public <T> Long add(T entity) throws ServiceException{

        StringBuilder sql = new StringBuilder("insert into ").append(this.entityTemplate.getTable()).append(" (");
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

            if (holder.getKey() != null) {
                Long id = holder.getKey().longValue();
                return id;
            }
            return null;
        }

    }

    /**
     * 更新记录
     * @param entity
     * @return
     * @throws ServiceException
     */
    @Override
    public <T> boolean update(T entity) throws ServiceException{
        return false;
    }

    /**
     * 只能在子类调用
     * @param entity
     * @param <T>
     * @return
     * @throws ServiceException
     */
    protected  <T> boolean updateEntity(T entity) throws ServiceException{
        if (null == this.entityTemplate || this.entityTemplate.isMultiPK()) {
            return false;
        }

        List<Object> params = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(this.entityTemplate.getTable()).append(" set ");
        boolean changed = false;
        Iterator<Field> iter = this.entityTemplate.getFields().iterator();
        while (iter.hasNext()) {
            Field field = iter.next();
            String name = field.getName();

            if (name.equalsIgnoreCase(this.entityTemplate.getPk())){
                continue;
            }

            try {
                Object val = field.get(entity);
                if (null != val) {
                    sb.append(field.getName()).append("= ? ,");
                    params.add(val);
                    changed = true;
                }

            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (!changed){
            return false;
        }

        sb.delete(sb.length() - 1, sb.length());
        sb.append("  where ").append(this.entityTemplate.getPk()).append(" = ? ");
        Object pkvalue = null;
        try {
            pkvalue = this.entityTemplate.getPkField().get(entity);
            params.add(pkvalue);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }

        int count = this.jdbcTemplateM.update(sb.toString().intern(), params.toArray());

        if (this.cacheBucket != null){
            String key = genCacheKey(":"+pkvalue);
            this.cacheBucket.remove(key);
        }

        return count > 0;
    }

    /**
     * 移除记录.
     * @param oid
     * @return
     * @throws ServiceException
     */
    @Override
    public boolean remove(Long oid) throws ServiceException{
        String sql;
        int count = 0;
        if (this.entityTemplate.isHasIfDeleted()){
            sql = String.format("update %s set ifDeleted=?, deleteAt=? where %s = ?", this.entityTemplate.getTable(), this.entityTemplate.getPk());
            count = this.jdbcTemplateM.update(sql, 1, new Date(), oid);
        }else{
            sql = String.format("delete from %s where %s = ?", this.entityTemplate.getTable(), this.entityTemplate.getPk());
            count = this.jdbcTemplateM.update(sql, oid);
        }
        this.expire(oid);
        return count > 0;
    }

    @Override
    public void expire(Long oid){
        String key = genCacheKey(":"+oid);
        if (this.cacheBucket != null){
            boolean flag = this.cacheBucket.remove(key);
            if (logger.isDebugEnabled()) {
                logger.debug("expire cache. key={}, result={}", key, flag);
            }
        }
    }
}
