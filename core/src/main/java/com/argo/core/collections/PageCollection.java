package com.argo.core.collections;

public abstract class PageCollection {
	
	private Integer pageIndex;
	private Integer pageSize;
	private Long totalItems;
	
	
	
	/**
	 * 
	 */
	public PageCollection() {
		super();
	}

	/**
	 * @param pageIndex
	 * @param pageSize
	 * @param totalItems
	 */
	public PageCollection(Integer pageIndex, Integer pageSize, Long totalItems) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.totalItems = totalItems;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}
	
	public Long getTotalPages(){
		if(this.pageSize==null || this.pageSize==0){
			return 0L;
		}
		
		long count = this.totalItems / this.pageSize;
		if(this.totalItems%this.pageSize>0){
			count ++;
		}
		return count;
	}
}
