package com.argo.core.collections;

import com.argo.core.base.BaseService;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;


/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-10-30
 */
public class LazyObject<T> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 
	 */
	private BaseService itemService;
	/**
	 * 
	 */
	private Serializable objectId;
	/**
	 * @param itemService
	 * @param objectId
	 */
	public LazyObject(BaseService itemService, Serializable objectId) {
		super();
		this.itemService = itemService;
		this.objectId = objectId;
	}
	
	public T getObject(){
		try {
			return (T) this.itemService.findById(this.objectId);
		} catch (ServiceException e) {
			log.error("读取实体详细错误:itemId="+this.objectId, e);
		} catch (EntityNotFoundException e) {
			log.error("读取实体详细错误:itemId="+this.objectId, e);
		}
		return null;
	}
}
