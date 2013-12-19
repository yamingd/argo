package com.argo.core.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 描述 ：假的LazyList,主要用来统一接口.
 *
 * @author yaming_deng
 */
public class LazyListF<T> extends LazyList<T> {

	private List<T> items = null;

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
