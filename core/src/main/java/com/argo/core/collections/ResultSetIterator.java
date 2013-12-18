package com.argo.core.collections;

import com.argo.core.base.BaseService;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;


/**
 * ResultSet迭代器
 * @author yaming_deng
 *
 * @param <E>
 */
public class ResultSetIterator<E> implements Iterator<E> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private BaseService itemGetter;
	private Iterator<Long> itor = null;	
	
	public ResultSetIterator(List<Long> itemIds,
			BaseService itemGetter) {
		
		super();
		
		Assert.notNull(itemIds);
		Assert.notNull(itemGetter);
		
		this.itemGetter = itemGetter;
		this.itor = itemIds.iterator();
	}

	@Override
	public boolean hasNext() {
		return itor.hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		Long itemId = itor.next();
		try {
			return (E) this.itemGetter.findById(itemId.intValue());
		} catch (ServiceException e) {
			log.error("读取实体详细错误:itemId="+itemId, e);
		} catch (EntityNotFoundException e) {
			log.error("读取实体详细错误:itemId="+itemId, e);
		}
		return null;
	}

	@Override
	public void remove() {
		this.itor.remove();
	}

}
