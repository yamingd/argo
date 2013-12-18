package com.argo.elasticsearch;

import java.io.Serializable;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

public interface SearchCriteria extends Serializable {
	
	/**
	 * org.elasticsearch.index.query.QueryBuilders
	 * @return
	 */
	QueryBuilder getQuery();
	
	/**
	 * 返回排序设置.
	 * @return
	 */
	List<SortBuilder> getSorts();
	<T extends SearchCriteria> T addSort(String filed, boolean asc);
	
	
	/**
	 * 返回的字段.
	 * @param fields
	 */
	void addFields(String... fields);
	List<String> getFields();
	
	/**
	 * Filter设置.
	 * org.elasticsearch.index.query.FilterBuilders
	 * @return
	 */
	FilterBuilder getFilter();
	
	/**
	 * 使用org.elasticsearch.search.facet.FacetBuilders构造.
	 * org.elasticsearch.search.facet.FacetBuilders
	 * @return
	 */
	List<FacetBuilder> getFacets();
	
	/**
	 * "Do you mean?"请求设置.
	 * org.elasticsearch.search.suggest.SuggestBuilder
	 * org.elasticsearch.search.suggest.term.TermSuggestionBuilder
	 * org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder
	 * org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder
	 * @return
	 */
	List<SuggestBuilder.SuggestionBuilder<?>> getSuggests();
	<T extends SearchCriteria> T addTermSuggest(String filed, String term);
}
