package com.argo.core.collections;

import com.argo.core.base.BaseService;
import com.argo.core.collections.RankResultSetItem;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ResultSet迭代器
 * @author yaming_deng
 *
 * @param <E>
 */
public class RankResultSetIterator<T> implements Iterator<RankResultSetItem<T>> {
	
	private BaseService itemGetter;
	private Iterator<Long> itor = null;	
	private Map<Long, Double> rankMap;
	
	public RankResultSetIterator(List<Long> itemIds,
			BaseService itemGetter, Map<Long, Double> rankMap) {
		
		super();
		
		Assert.notNull(itemIds);
		Assert.notNull(itemGetter);
		
		this.itemGetter = itemGetter;
		this.itor = itemIds.iterator();
		this.rankMap = rankMap;
	}

	@Override
	public boolean hasNext() {
		return itor.hasNext();
	}

	@Override
	public RankResultSetItem<T> next() {
		Long itemId = itor.next();
		return new RankResultSetItem<T>(itemGetter, itemId, rankMap.get(itemId));
	}

	@Override
	public void remove() {
		this.itor.remove();
	}
}
