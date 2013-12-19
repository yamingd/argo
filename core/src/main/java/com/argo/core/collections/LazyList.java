package com.argo.core.collections;


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
		
	private List<Long> itemIds;
	private Integer size;
	
	@Override
	public Iterator<T> iterator() {
		return new LazyListIterator<T>(this.itemIds);
	}
	
	public LazyList() {
		super();
	}

	public LazyList(List<Long> itemIds) {
		super(1,0,itemIds.size()*1L);
		this.itemIds = itemIds;
	}

	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}

	public List<Long> getItemIds() {
		return itemIds;
	}
	
	public void filter(List<Long> ids){
		if(this.itemIds!=null && ids != null){
			this.itemIds.removeAll(ids);
		}
	}

	public Integer getSize() {
		this.size = this.itemIds.size();
		return this.size;
	}

	public void addAll(Integer index, List<Long> ids){
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
