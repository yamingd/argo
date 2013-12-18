package com.argo.core.collections;

import com.argo.core.base.BaseService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 描述 ：假的LazyList,主要用来统一接口.
 *
 * @author yaming_deng
 * @date 2012-3-26
 */
public class LazyListF<T> extends LazyList<T> {

	private List<T> items = null;
	/**
	 * @param itemService
	 */
	@Deprecated
	public LazyListF(BaseService itemService) {
		super(itemService);
	}

	/**
	 * @param itemIds
	 * @param itemService
	 */
	@Deprecated
	public LazyListF(List<Integer> itemIds, BaseService itemService) {
		super(itemIds, itemService);
	}

	/**
	 * @param items
	 */
	public LazyListF(List<T> items) {
		this.items = items;
	}
	
	public LazyListF() {
		this.items = new ArrayList<T>();
	}

	@Override
	public Iterator<T> iterator() {
		if(this.items == null){
			throw new IllegalArgumentException("items is null.");
		}
		return items.iterator();
	}
	
	public Integer getSize() {
		return this.items.size();
	}
	
	public List<T> asList(){
		return this.items;
	}
	
	public void addItem(T item){
		this.items.add(item);
	}
}
