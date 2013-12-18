package com.argo.core.data;

import com.argo.core.base.BaseEntity;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.RepositoryException;

import java.io.Serializable;
import java.util.Collection;

/**
 * DAO Templates
 * Created with IntelliJ IDEA.
 * User: yamingdeng
 * Date: 13-11-19
 * Time: 下午8:19
 */
public interface RepositoryTemplate<T extends BaseEntity, PK extends Serializable> {

    /**
     * 生成主键
     * @param <PK>
     * @return
     */
    <PK> PK genPK();

    /**
     * 新增记录
     * @param entity
     * @throws RepositoryException
     */
    void insert(T entity) throws RepositoryException;

    /**
     * 批量新增记录
     * @param entities
     * @throws RepositoryException
     */
    void insert(Collection<T> entities) throws RepositoryException;

    /**
     * 更新记录某些字段
     * @param entity
     * @throws RepositoryException
     */
    void update(T entity) throws RepositoryException;

    /**
     * 更新记录某些字段
     * @param entities
     * @throws RepositoryException
     */
    void update(Collection<T> entities) throws RepositoryException;

    /**
     * 更新记录
     * @param entity
     * @throws RepositoryException
     */
    void save(T entity) throws RepositoryException;

    /**
     * 更新记录
     * @param entities
     * @throws RepositoryException
     */
    void save(Collection<T> entities) throws RepositoryException;

    /**
     * 删除记录
     * @param entity
     * @throws RepositoryException
     */
    void remove(T entity) throws RepositoryException;

    /**
     * 删除记录
     * @param entities
     * @throws RepositoryException
     */
    void remove(Collection<T> entities) throws RepositoryException;

    /**
     * 按主键查找记录
     * @param id
     * @param <T>
     * @return
     * @throws EntityNotFoundException
     */
    <T> T findById(PK id) throws EntityNotFoundException;
}
