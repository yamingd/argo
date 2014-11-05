package com.argo.db.template;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;

import java.util.List;

/**
 * Created by yaming_deng on 14-8-28.
 */
public interface ServiceBase {
    /**
     * 读取详情
     * @param oid
     * @return
     * @throws com.argo.core.exception.ServiceException
     */
    <T> T findById(Long oid)throws EntityNotFoundException;

    /**
     * 读取一系列id的值
     * @param oids
     * @param <T>
     * @return
     */
    <T> List<T> findByIds(List<Long> oids);
    /**
     * 添加记录
     * @param entity
     * @return
     * @throws com.argo.core.exception.ServiceException
     */
    <T> Long add(T entity) throws ServiceException;

    /**
     * 更新记录
     * @param entity
     * @return
     * @throws ServiceException
     */
    <T> boolean update(T entity) throws ServiceException;

    /**
     * 移除记录.
     * @param oid
     * @return
     * @throws ServiceException
     */
    boolean remove(Long oid) throws ServiceException;

    /**
     * 删除缓存
     * @param oid
     * @return
     */
    void expire(Long oid);
}
