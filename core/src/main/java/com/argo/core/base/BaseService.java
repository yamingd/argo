package com.argo.core.base;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;

import java.io.Serializable;
import java.util.List;


public interface BaseService<T extends BaseEntity> {

    /**
     * 保存一个实体对象
     * @param entity 实体对象
     * @throws ServiceException
     */
    void createEntity(T entity) throws ServiceException;
    
    /**
     * 更新一个实体对象
     * @param entity 实体对象
     * @throws ServiceException
     * @throws EntityNotFoundException
     */
    void updateEntity(T entity) throws ServiceException;
    
    /**
     * 删除一个实体对象.
     * @param entity
     * @throws ServiceException
     */
    void deleteEntity(T entity) throws ServiceException;
    /**
     * 按主键编号查找一个实体对象
     * @param id 主键编号
     * @return Object [实体对象]
     * @throws ServiceException
     * @throws EntityNotFoundException
     */
    <T>T findById(Serializable id) throws ServiceException, EntityNotFoundException;
    /**
	 * 遍历数据库记录. 用来建立全文索引
	 * 取出从startId开始的limit条记录，结果集按主键升序排序.
	 * @param startId
	 * @param limit
	 * @return
	 * @throws ServiceException
	 */
	public List<T> iteratorEntityItems(Integer startId, Integer limit) throws ServiceException;
}
