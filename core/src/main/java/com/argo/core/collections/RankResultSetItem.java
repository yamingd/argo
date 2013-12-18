package com.argo.core.collections;


import com.argo.core.base.BaseService;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;

public class RankResultSetItem<T> {
	
	private BaseService itemGetter;
	private Long itemId;
	private Double score;
	private Integer intScore;
	
	public BaseService getItemGetter() {
		return itemGetter;
	}
	
	public Long getItemId() {
		return itemId;
	}
	
	public Double getScore() {
		return score;
	}
	
	
	public RankResultSetItem(BaseService itemGetter, Long itemId,
			Double score) {
		super();
		this.itemGetter = itemGetter;
		this.itemId = itemId;
		this.score = score;
		this.intScore = score.intValue();
	}

	public Integer getIntScore() {
		return intScore;
	}

	@SuppressWarnings("unchecked")
	public T getEntity() throws ServiceException, EntityNotFoundException {
		return (T) this.itemGetter.findById(itemId.intValue());
	}

	public T getSafeEntity(){
		try{
			return this.getEntity();
		}catch(Exception ex){
            //TODO:LOGGING
			return null;
		}
	}
}

