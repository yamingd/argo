package com.argo.core.collections;

import com.argo.core.base.BaseService;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 本类实现排行榜系列的集合
 * @author yaming_deng
 *
 * @param <T>
 */
public class RankResultSet<T> extends PageCollection implements Iterable<RankResultSetItem<T>> {
	
	private Map<Long, Double> rankMap;
	private List<Long> itemIds;
	private BaseService itemGetter;
		
	public RankResultSet() {
		super();
	}

	public RankResultSet(BaseService itemGetter) {
		super();
		Assert.notNull(itemGetter);
		this.itemGetter = itemGetter;
	}

	public RankResultSet(List<Long> itemIds, Map<Long, Double> rankMap, BaseService itemGetter) {
		super();
		Assert.notNull(rankMap);
		Assert.notNull(itemGetter);
		Assert.notNull(itemIds);
		
		this.rankMap = rankMap;
		this.itemGetter = itemGetter;
		this.itemIds = itemIds;
	}
		
	@Override
	public Iterator<RankResultSetItem<T>> iterator() {
		return new RankResultSetIterator<T>(this.itemIds,this.itemGetter,this.rankMap);
	}

	
	public BaseService getItemGetter() {
		return itemGetter;
	}

	public void setItemGetter(BaseService itemGetter) {
		this.itemGetter = itemGetter;
	}
	
	public void setRankMap(Map<Long, Double> rankMap) {
		this.rankMap = rankMap;
	}

	public Map<Long, Double> getRankMap() {
		return rankMap;
	}

	public void setItemIds(List<Long> itemIds) {
		this.itemIds = itemIds;
	}

	public List<Long> getItemIds() {
		return itemIds;
	}
	public Integer getSize() {
		return itemIds.size();
	}
}
