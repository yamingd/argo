package com.argo.core.collections;


import com.argo.core.base.BaseService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 延迟加载列表项.
 * @author yaming_deng
 *
 * @param <T>
 */
public class LazyList<T> extends PageCollection implements Iterable<T>  {
		
	private List<Integer> itemIds;
	private BaseService itemService;
	private Integer size;
	
	@Override
	public Iterator<T> iterator() {
		return new LazyListIterator<T>(this.itemIds,this.itemService);
	}
	
	public LazyList() {
		super();
	}
	
	public LazyList(BaseService itemService) {
		super();
		this.itemService = itemService;
	}
	
	public LazyList(List<Integer> itemIds, BaseService itemService) {
		super(1,0,itemIds.size()*1L);
		this.itemIds = itemIds;
		this.itemService = itemService;
	}

	public void setItemIds(List<Integer> itemIds) {
		this.itemIds = itemIds;
	}

	public List<Integer> getItemIds() {
		return itemIds;
	}
	
	public void filter(List<Integer> ids){
		if(this.itemIds!=null && ids != null){
			this.itemIds.removeAll(ids);
		}
	}
	
	public void setItemService(BaseService itemService) {
		this.itemService = itemService;
	}

	public BaseService getItemService() {
		return itemService;
	}

	public Integer getSize() {
		this.size = this.itemIds.size();
		return this.size;
	}

	public void addAll(Integer index, List<Integer> ids){
		if(this.itemIds!=null && ids != null){
			for(Integer i=0;i<ids.size();i++){
				if(this.itemIds.contains(ids.get(i))){
					this.itemIds.remove(ids.get(i));
				}
				this.itemIds.add(index+i, ids.get(i));
			}
		}
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
