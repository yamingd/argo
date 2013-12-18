package com.argo.search;

import java.io.Serializable;
import java.util.List;

import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.suggest.Suggest;

public class SearchResult<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1658386222662117117L;
	
	private Long total;
	private Integer pageIndex = 0;
	private Integer pageSize = 20;
	private Integer pages;
	private List<T> items;
	private Facets facets;
	private Suggest suggest;
	
	public SearchResult(Long total, Integer pageIndex, Integer pageSize) {
		this.total = total;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		if(pageSize > 0){
			this.pages = total.intValue() / pageSize;
			if(total % pageSize > 0){
				this.pages += 1;
			}
		}else{
			this.pages = -1;
		}
	}
	
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
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
	public Integer getPages() {
		return pages;
	}
	public void setPages(Integer pages) {
		this.pages = pages;
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}

	public Facets getFacets() {
		return facets;
	}

	public void setFacets(Facets facets) {
		this.facets = facets;
	}

	public Suggest getSuggest() {
		return suggest;
	}

	public void setSuggest(Suggest suggest) {
		this.suggest = suggest;
	}
}
