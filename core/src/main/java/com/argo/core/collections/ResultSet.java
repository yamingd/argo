package com.argo.core.collections;

import com.argo.core.base.BaseService;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 延迟读取记录详细的记录集合实现.通常用于向量缓存层、搜索层.
 * @author yaming_deng
 *
 * @param <T>
 */
public class ResultSet<T> extends PageCollection implements Iterable<T> {
	
	private List<Long> itemIds;
	private BaseService itemGetter;
		
	public ResultSet() {
		super();
	}

	public ResultSet(BaseService itemGetter) {
		super();
		Assert.notNull(itemGetter);
		this.itemGetter = itemGetter;
	}

	public ResultSet(List<Long> itemIds, BaseService itemGetter) {
		super();
		Assert.notNull(itemIds);
		Assert.notNull(itemGetter);
		this.itemIds = itemIds;
		this.itemGetter = itemGetter;
	}

	@Override
	public Iterator<T> iterator() {
		return new ResultSetIterator<T>(this.itemIds,this.itemGetter);
	}


	public List<Long> getItemIds() {
		return itemIds;
	}

	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}

	public BaseService getItemGetter() {
		return itemGetter;
	}

	public void setItemGetter(BaseService itemGetter) {
		this.itemGetter = itemGetter;
	}
		
	public Integer getSize(){
		return this.itemIds.size();
	}
	
	public List<T> asList(){
		List<T> result = new ArrayList<T>();
		Iterator<T> iterator = this.iterator();
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}
}
