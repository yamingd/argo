package com.argo.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.datehistogram.DateHistogramFacetBuilder;
import org.elasticsearch.search.facet.histogram.HistogramFacetBuilder;
import org.elasticsearch.search.facet.range.RangeFacetBuilder;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.util.Assert;

public class SearchCriteriaImpl implements SearchCriteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8113279849201180759L;

	protected List<SortBuilder> sorts = new ArrayList<SortBuilder>();
	protected List<String> fields = new ArrayList<String>();
	private FilterBuilder filter;
	private List<FacetBuilder> facets = new ArrayList<FacetBuilder>();
	private List<SuggestBuilder.SuggestionBuilder<?>> suggests = new ArrayList<SuggestBuilder.SuggestionBuilder<?>>();
	private QueryBuilder query = null;

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SearchCriteria> T addSort(String filed, boolean asc) {
		SortBuilder s = new FieldSortBuilder(filed);
		if (asc) {
			s.order(SortOrder.ASC);
		} else {
			s.order(SortOrder.DESC);
		}
		return (T) this;
	}

	@Override
	public void addFields(String... fields) {
		CollectionUtils.addAll(this.fields, fields);
	}

	@Override
	public List<String> getFields() {
		return fields;
	}

	@Override
	public List<SortBuilder> getSorts() {
		return sorts;
	}

	@Override
	public FilterBuilder getFilter() {
		return filter;
	}

	public void setFilter(FilterBuilder filter) {
		this.filter = filter;
	}

	@Override
	public List<FacetBuilder> getFacets() {
		return facets;
	}

	@SuppressWarnings("unchecked")
	public <T extends SearchCriteria> T addFacet(FacetBuilder facet) {
		this.facets.add(facet);
		return (T) this;
	}

	/**
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/
	 * search-facets-range-facet.html
	 * 
	 * @param name
	 * @param field
	 * @return
	 */
	public RangeFacetBuilder addRangeFacet(String name, String field) {
		RangeFacetBuilder builder = FacetBuilders.rangeFacet(name).field(field);
		this.facets.add(builder);
		return builder;
	}

	/**
	 * 添加时间区间Facet.
	 * 
	 * @param name
	 *            Facetd的名字
	 * @param field
	 *            实施的字段名字.
	 * @param interval
	 *            {@link FacetConstants}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends SearchCriteria> T addDateHistogram(String name,
			String field, String interval) {
		DateHistogramFacetBuilder builder = FacetBuilders.dateHistogramFacet(
				name).field(field);
		builder.interval(interval);
		this.facets.add(builder);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends SearchCriteria> T addHistogram(String name, String field,
			int interval) {
		HistogramFacetBuilder builder = FacetBuilders.histogramFacet(name)
				.field(field);
		builder.interval(interval);
		this.facets.add(builder);
		return (T) this;
	}

	@Override
	public List<SuggestBuilder.SuggestionBuilder<?>> getSuggests() {
		return suggests;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SearchCriteria> T addTermSuggest(String field, String term) {
		Assert.notNull(field, "parameter field is NULL.");
		Assert.notNull(term, "parameter term is NULL.");
		TermSuggestionBuilder builder = SuggestBuilder.termSuggestion(field);
		builder.field(field).text(term);
		suggests.add(builder);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends SearchCriteria> T addTermFacet(String name, int size,
			String... fields) {
		Assert.notNull(name, "parameter name is NULL.");
		Assert.notNull(size, "parameter size is NULL.");
		TermsFacetBuilder builder = FacetBuilders.termsFacet(name)
				.fields(fields).size(size);
		facets.add(builder);
		return (T) this;
	}

	@Override
	public QueryBuilder getQuery() {
		return query;
	}

	public void setQuery(QueryBuilder query) {
		this.query = query;
	}

}
