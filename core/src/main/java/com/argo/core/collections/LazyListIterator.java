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
 * LazyList迭代器
 * @author yaming_deng
 *
 * @param <E>
 */
public class LazyListIterator<E> implements Iterator<E> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private BaseService itemService;
	private Iterator<Integer> itor = null;	
	
	public LazyListIterator(List<Integer> itemIds,
			BaseService itemService) {
		
		super();
		
		Assert.notNull(itemIds);
		Assert.notNull(itemService);
		
		this.itemService = itemService;
		this.itor = itemIds.iterator();
	}

	@Override
	public boolean hasNext() {
		return itor.hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		Integer itemId = itor.next();
		try {
			return (E) this.itemService.findById(itemId);
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
