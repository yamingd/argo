package com.argo.elasticsearch;

import com.argo.core.base.BaseEntity;
import com.argo.search.SearchException;
import com.argo.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.mlt.MoreLikeThisRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder.SuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticSearchTemplate {

    protected Logger logger = null;

	private Client client;
	private String indexName;
	private ESDocumentMapper docMapper = null;
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
	public ESDocumentMapper getDocMapper() {
		return docMapper;
	}

	public void setDocMapper(ESDocumentMapper docMapper) {
		this.docMapper = docMapper;
	}
	
	public ElasticSearchTemplate(){
		super();
        logger = LoggerFactory.getLogger(getClass());
	}
	
	public ElasticSearchTemplate(final Client client, final String indexName,
			final ESDocumentMapper docMapper) {
		super();
		this.client = client;
		this.indexName = indexName;
		this.docMapper = docMapper;
        logger = LoggerFactory.getLogger(getClass()+"."+indexName);
	}
	
	public ElasticSearchTemplate(final Client client, final String indexName) {
		this(client, indexName, new DefaultDocMapper());
        logger = LoggerFactory.getLogger(getClass()+"."+indexName);
	}
	
	/**
	 * 添加索引.
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-index_.html
	 * @param entity
	 * @return
	 */
	public <T> boolean add(T entity) throws SearchException{
		if (entity == null) {
			return false;
		}
        if (!(entity instanceof BaseEntity)){
            throw new SearchException("entity must inherit from BaseEntity.");
        }
		try {
            String id = "";
            if (entity instanceof BaseEntity){
               id = ((BaseEntity)entity).getPK();
            }
			String typeName = this.getTypeNameFromClass(entity.getClass());
			String jsonSource = this.docMapper.toJSON(entity, false);
			IndexRequestBuilder inserter = client.prepareIndex(indexName,
					typeName, id);
			inserter.setSource(jsonSource);
			inserter.execute().actionGet();
			return true;
		} catch (Exception e) {
			logger.error("add index Error.", e);
            throw new SearchException("add index Error.", e);
		}
	}

	/**
	 * 批量索引.
	 * 
	 * @param items
	 * @return
	 */
	public <T> boolean bulkAdd(List<T> items) throws SearchException {
		if (items == null || items.size() == 0) {
			return false;
		}
        for (T item : items){
            if (!(item instanceof BaseEntity)){
                throw new SearchException("item must inherit from BaseEntity.");
            }
        }
		try {
			String typeName = this
					.getTypeNameFromClass(items.get(0).getClass());
			BulkRequestBuilder bulk = new BulkRequestBuilder(client);
			for (T item : items) {
				if (item == null) {
					continue;
				}

				String iid = ((BaseEntity)item).getPK();
				IndexRequestBuilder inserter = client.prepareIndex(indexName,
						typeName, iid);
				inserter.setSource(this.docMapper.toJSON(item, false));
				bulk.add(inserter);
			}
			BulkResponse actionGet = bulk.execute().actionGet();
			if (actionGet.hasFailures()) {
				return false;
			}
			return true;
		} catch (Exception e) {
            logger.error("bulkAdd index Error.", e);
            throw new SearchException("bulkAdd index Error.", e);
		}
	}

	public boolean add(String typeName, String id, Map<String, Object> data)  throws SearchException {
		if (data == null || data.size() == 0) {
			return false;
		}
		try {
			String jsonSource = this.docMapper.toJSON(data, false);
			IndexRequestBuilder inserter = client.prepareIndex(indexName,
					typeName, id);
			inserter.setSource(jsonSource);
			inserter.execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("add Map index Error.", e);
            throw new SearchException("add Map index Error.", e);
		}
	}

	/**
	 * 删除索引.
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-delete.html
	 * @param typeName
	 * @param id
	 * @return
	 */
	public boolean delete(String typeName, String id)  throws SearchException {
		try {
			DeleteRequestBuilder deleter = client.prepareDelete(indexName,
					typeName, id);
			DeleteResponse actionGet = deleter.execute().actionGet();
			if (!actionGet.isNotFound()) {
				return true;
			}
			return false;
		} catch (Exception e) {
            logger.error("delete index Error. ", e);
            throw new SearchException("delete index Error. typeName="+typeName+", id="+id, e);
		}
	}

	public boolean delete(String typeName, String... ids)  throws SearchException {
		try {
			BulkRequestBuilder bulk = new BulkRequestBuilder(client);
			for (String id : ids) {
				DeleteRequestBuilder deleter = client.prepareDelete(indexName,
						typeName, id);
				bulk.add(deleter);
			}
			BulkResponse actionGet = bulk.execute().actionGet();
			if (actionGet.hasFailures()) {
				return false;
			}
			return true;
		} catch (Exception e) {
            logger.error("delete index Error. ", e);
            throw new SearchException("delete index Error. typeName="+typeName+", ids="+ids, e);
		}
	}

	public boolean delete(Class<?> clzz, String id)  throws SearchException {
        String typeName = getTypeNameFromClass(clzz);
		try {
			DeleteRequestBuilder deleter = client.prepareDelete(indexName,
					typeName, id);
			DeleteResponse actionGet = deleter.execute().actionGet();
			if (!actionGet.isNotFound()) {
				return true;
			}
			return false;
		} catch (Exception e) {
            logger.error("delete index Error. ", e);
            throw new SearchException("delete index Error. typeName="+typeName+", id="+id, e);
		}
	}

	public boolean delete(Class<?> clzz, String... ids) throws SearchException {
        String typeName = getTypeNameFromClass(clzz);
		try {

			BulkRequestBuilder bulk = new BulkRequestBuilder(client);
			for (String id : ids) {
				DeleteRequestBuilder deleter = client.prepareDelete(indexName,
						typeName, id);
				bulk.add(deleter);
			}
			BulkResponse actionGet = bulk.execute().actionGet();
			if (actionGet.hasFailures()) {
				return false;
			}
			return true;
		} catch (Exception e) {
            logger.error("delete index Error. ", e);
            throw new SearchException("delete index Error. typeName="+typeName+", ids="+ids, e);
		}
	}

	public boolean deleteByQuery(String typeName, String query) throws SearchException {
		try {
			client.prepareDeleteByQuery(indexName).setTypes(typeName)
					.setQuery(query).execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("deleteByQuery index Error. ", e);
            throw new SearchException("deleteByQuery index Error. typeName="+typeName+", query="+query, e);
		}
	}

	public boolean deleteByQuery(Class<?> clzz, String query) throws SearchException {
        String typeName = getTypeNameFromClass(clzz);
		try {
			client.prepareDeleteByQuery(indexName).setTypes(typeName)
					.setQuery(query).execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("deleteByQuery index Error. ", e);
            throw new SearchException("deleteByQuery index Error. typeName="+typeName+", query="+query, e);
		}
	}

	/**
	 * 更新索引
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-update.html
	 * @param entity
	 * @return
	 */
	public <T> boolean update(T entity) throws SearchException {
		if (entity == null) {
			return false;
		}
        if (!(entity instanceof BaseEntity)){
            throw new SearchException("entity must inherit from BaseEntity.");
        }

        String typeName = this.getTypeNameFromClass(entity.getClass());
        String id = ((BaseEntity)entity).getPK();

		try {
			String jsonSource = this.docMapper.toJSON(entity, true);
			UpdateRequestBuilder request = client.prepareUpdate(indexName,
					typeName, id).setId(id);
			request.setDoc(jsonSource);
			request.execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("update index Error. ", e);
            throw new SearchException("update index Error. typeName="+typeName+", id="+id, e);
		}
	}

	public <T> boolean update(T entity, boolean upset) throws SearchException {
		if (entity == null) {
			return false;
		}
        if (!(entity instanceof BaseEntity)){
            throw new SearchException("entity must inherit from BaseEntity.");
        }

        String typeName = this.getTypeNameFromClass(entity.getClass());
        String id = ((BaseEntity)entity).getPK();

		try {
			String jsonSource = this.docMapper.toJSON(entity, true);
			UpdateRequestBuilder request = client.prepareUpdate(indexName,
					typeName, id).setId(id);
			if (upset) {
				request.setDocAsUpsert(true);
			}
			request.setDoc(jsonSource);
			request.execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("update index Error. ", e);
            throw new SearchException("update index Error. typeName="+typeName+", id="+id, e);
		}
	}

    public <T> boolean update(Class<T> clazz, String id, Map<String, Object> data,
                          boolean upset) throws SearchException {
        if (data == null || data.size() == 0) {
            return false;
        }
        String typeName = this.getTypeNameFromClass(clazz);
        return this.update(typeName, id, data, upset);
    }

	public boolean update(String typeName, String id, Map<String, Object> data,
			boolean upset) throws SearchException {
		if (data == null || data.size() == 0) {
			return false;
		}
		try {
			String jsonSource = this.docMapper.toJSON(data, true);
			UpdateRequestBuilder request = client.prepareUpdate(indexName,
					typeName, id).setId(id);
			if (upset) {
				request.setDocAsUpsert(true);
			}
			request.setDoc(jsonSource);
			request.execute().actionGet();
			return true;
		} catch (Exception e) {
            logger.error("update index Error. ", e);
            throw new SearchException("update index Error. typeName="+typeName+", id="+id, e);
		}
	}

	/**
	 * 自增/自减某些字段的统计值.
	 * 
	 * @param clazz
	 * @param id
	 * @param amounts
	 * @return
	 */
    public <T> boolean updateIncr(Class<T> clazz, String id,
                              Map<String, Integer> amounts) throws SearchException {
        String typeName = this.getTypeNameFromClass(clazz);
        return this.updateIncr(typeName, id, amounts);
    }
	public boolean updateIncr(String typeName, String id,
			Map<String, Integer> amounts) throws SearchException {
		if (amounts == null || amounts.size() == 0) {
			return false;
		}
		try {
			BulkRequestBuilder bulk = new BulkRequestBuilder(client);
			for (String field : amounts.keySet()) {
				UpdateRequestBuilder updater = client.prepareUpdate(indexName,
						typeName, id);
				updater.setId(id);
				updater.setScript(String.format("ctx._source.%s += count",
						field));
				updater.addScriptParam("count", amounts.get(field));
				bulk.add(updater);
			}
			BulkResponse actionGet = bulk.execute().actionGet();
			if (actionGet.hasFailures()) {
				return false;
			}
			return true;
		} catch (Exception e) {
            logger.error("updateIncr Error. ", e);
            throw new SearchException("updateIncr Error. typeName="+typeName+", id="+id, e);
		}
	}
	
	public <T> boolean exists(Class<T> clzz, String id) throws SearchException {
        String typeName = this.getTypeNameFromClass(clzz);
		try {
			GetResponse response = client.prepareGet(indexName, typeName, id).setFields("uid")
					.execute().actionGet();
			return response.isExists();
		} catch (Exception e) {
            logger.error("exists check Error. ", e);
            throw new SearchException("exists check Error. typeName="+typeName+", id="+id, e);
		}
	}
	
	public <T> boolean exists(Class<T> clzz, String id, String idField) throws SearchException {
        String typeName = this.getTypeNameFromClass(clzz);
		try {
			GetResponse response = client.prepareGet(indexName, typeName, id).setFields(idField)
					.execute().actionGet();
			return response.isExists();
		} catch (Exception e) {
            logger.error("exists check Error. ", e);
            throw new SearchException("exists check Error. typeName="+typeName+", id="+id, e);
		}
	}
	/**
	 * 按id读取.
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-get.html
	 * @param id
	 * @param clzz
	 * @return
	 */
	public <T> T queryForObject(Class<T> clzz, String id) throws SearchException {
        String typeName = this.getTypeNameFromClass(clzz);
		try {
			GetResponse response = client.prepareGet(indexName, typeName, id)
					.execute().actionGet();
			if(response.isSourceEmpty()){
				return null;
			}
			if(response.isExists()){
				return null;
			}
			T o = this.docMapper.asObject(clzz, response.getSourceAsString());
			return o;
		} catch (Exception e) {
            logger.error("queryForObject Error. ", e);
            throw new SearchException("queryForObject Error. typeName="+typeName+", id="+id, e);
		}
	}
	
	/**
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/docs-multi-get.html
	 * @param clzz
	 * @param ids
	 * @return
	 */
	public <T> List<T> queryForObjects(Class<T> clzz, String... ids) throws SearchException {
        String typeName = this.getTypeNameFromClass(clzz);
		try {
			MultiGetRequestBuilder request = client.prepareMultiGet();
			request.add(indexName, typeName, ids);
			MultiGetResponse response = request.execute().actionGet();
			List<T> results = new ArrayList<T>();
			for(MultiGetItemResponse resp : response.getResponses()){
				if(!resp.isFailed()){
					T o = this.docMapper.asObject(clzz, resp.getResponse().getSourceAsString());
					results.add(o);
				}
			}
			return results;
		} catch (Exception e) {
            logger.error("queryForObjects Error. ", e);
            throw new SearchException("queryForObjects Error. typeName="+typeName+", ids="+ids, e);
		}
	}
	/**
	 * 仅返回ids.
	 * @param typeName
	 * @param query
	 * @return
	 */
	public <T> List<String> queryForIds(String typeName, SearchCriteria query, int page, int size) throws SearchException {
		try {
			int start = page < 1 ? 0 : (page - 1) * size;
			SearchRequestBuilder request = client
					.prepareSearch(indexName)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setTypes(typeName).setQuery(query.getQuery()).setNoFields();
			request.setFrom(start).setSize(size);
			SearchResponse response = request.execute().actionGet();
			return extractIds(response);
		} catch (Exception e) {
            logger.error("queryForIds Error. ", e);
            throw new SearchException("queryForIds Error. typeName="+typeName, e);
		}
	}
	public <T> List<String> queryForIds(Class<T> clzz, SearchCriteria query, int page, int size) throws SearchException {
        String typeName = this.getTypeNameFromClass(clzz);
		try {
			int start = page < 1 ? 0 : (page - 1) * size;
			SearchRequestBuilder request = client
					.prepareSearch(indexName)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setTypes(typeName).setQuery(query.getQuery()).setNoFields();
			request.setFrom(start).setSize(size);
			SearchResponse response = request.execute().actionGet();
			return extractIds(response);
		} catch (Exception e) {
            logger.error("queryForIds Error. ", e);
            throw new SearchException("queryForIds Error. typeName="+typeName, e);
		}
	}
		
	/**
	 * 检索.
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-search.html
	 * @param query
	 * @param page
	 * @param size
	 * @param types
	 * @return
	 */
	public <T> SearchResult<T> search(Class<T> clazz, SearchCriteria query, int page, int size, String... types) throws SearchException {
		try {
			int start = page < 1 ? 0 : (page - 1) * size;
			SearchRequestBuilder request = client
					.prepareSearch(indexName)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setTypes(types)
					.setFrom(start).setSize(size)
					.setExplain(false);
			if(query.getQuery()!=null){
				request.setQuery(query.getQuery());
			}
			if(query.getSorts()!=null && query.getSorts().size()>0){
				for (SortBuilder item : query.getSorts()) {
					request.addSort(item);
				}
			}
			if(query.getFields()!=null && query.getFields().size()>0){
				request.addFields(this.toArray(query.getFields()));
			}
			if(query.getFilter()!=null){
				request.setFilter(query.getFilter());
			}
			if(query.getFacets()!=null && query.getFacets().size() > 0){
				for(FacetBuilder facet : query.getFacets()){
					request.addFacet(facet);
				}
			}
			if(query.getSuggests()!=null && query.getSuggests().size() > 0){
				for(SuggestionBuilder<?> builder : query.getSuggests()){
					request.addSuggestion(builder);
				}
			}
			SearchResponse response = request.execute().actionGet();
			return parseSearchResponse(clazz, page, size, response);
		} catch (Exception e) {
            logger.error("search Error. ", e);
            throw new SearchException("search Error. typeName="+types, e);
		}
	}

	protected <T> SearchResult<T> parseSearchResponse(Class<T> clazz, int page, int size,
			SearchResponse response) {
		if (response == null) {
			return null;
		}
		SearchHits hits = response.getHits();
		SearchResult<T> result = new SearchResult<T>(hits.getTotalHits(), page, size);
		List<T> items = new ArrayList<T>();
		SearchHit[] buf = hits.getHits();
		if(buf!=null){
			for (SearchHit searchHit : buf) {
				T item = this.docMapper.asObject(clazz, searchHit.getSourceAsString());
				items.add(item);
			}
		}
		result.setSuggest(response.getSuggest());
		result.setFacets(response.getFacets());
		result.setItems(items);
		return result;
	}
	
	/**
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-count.html
	 * @param query
	 * @param types
	 * @return
	 */
	public <T> long count(SearchCriteria query, String... types) throws SearchException {
		try {
			CountRequestBuilder request = client.prepareCount(indexName).setTypes(types);
			if(query!=null && query.getQuery()!=null){
				request.setQuery(query.getQuery());
			}
			return request.execute().actionGet().getCount();
		} catch (Exception e) {
            logger.error("count Error. ", e);
            throw new SearchException("count Error. typeName="+types, e);
		}
	}
	public <T> long count(SearchCriteria query, Class<?> clazz) {
        String typeName = this.getTypeNameFromClass(clazz);
		try {
			CountRequestBuilder request = client.prepareCount(indexName).setTypes(typeName);
			if(query!=null && query.getQuery()!=null){
				request.setQuery(query.getQuery());
			}
			return request.execute().actionGet().getCount();
		} catch (Exception e) {
            logger.error("count Error. ", e);
            throw new SearchException("count Error. typeName="+typeName, e);
		}
	}
	
	/**
	 * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-more-like-this.html
	 * @param query
	 * @param clazz
	 * @param page
	 * @param size
	 * @return
	 */
	public <T> SearchResult<T> moreLikeThis(MoreLikeThisQuery query, Class<T> clazz, int page, int size) throws SearchException {
        Assert.notNull(query.getId(), "No document id defined for MoreLikeThisQuery");
        
        String typeName = this.getTypeNameFromClass(clazz);
        MoreLikeThisRequestBuilder requestBuilder = client.prepareMoreLikeThis(indexName, typeName, query.getId());
        
        int start = page < 1 ? 0 : (page - 1) * size;
        requestBuilder.setSearchSize(size);
        requestBuilder.setSearchFrom(start);

        if (CollectionUtils.isNotEmpty(query.getSearchIndices())) {
            requestBuilder.setSearchIndices(toArray(query.getSearchIndices()));
        }
        if (CollectionUtils.isNotEmpty(query.getSearchTypes())) {
            requestBuilder.setSearchTypes(toArray(query.getSearchTypes()));
        }
        if (CollectionUtils.isNotEmpty(query.getFields())) {
            requestBuilder.setField(toArray(query.getFields()));
        }
        if (StringUtils.isNotBlank(query.getRouting())) {
            requestBuilder.setRouting(query.getRouting());
        }
        if (query.getPercentTermsToMatch() != null) {
            requestBuilder.setPercentTermsToMatch(query.getPercentTermsToMatch());
        }
        if (query.getMinTermFreq() != null) {
            requestBuilder.setMinTermFreq(query.getMinTermFreq());
        }
        if (query.getMaxQueryTerms() != null) {
            requestBuilder.maxQueryTerms(query.getMaxQueryTerms());
        }
        if (CollectionUtils.isNotEmpty(query.getStopWords())) {
            requestBuilder.setStopWords(toArray(query.getStopWords()));
        }
        if (query.getMinDocFreq() != null) {
            requestBuilder.setMinDocFreq(query.getMinDocFreq());
        }
        if (query.getMaxDocFreq() != null) {
            requestBuilder.setMaxDocFreq(query.getMaxDocFreq());
        }
        if (query.getMinWordLen() != null) {
            requestBuilder.setMinWordLen(query.getMinWordLen());
        }
        if (query.getMaxWordLen() != null) {
            requestBuilder.setMaxWordLen(query.getMaxWordLen());
        }
        if (query.getBoostTerms() != null) {
            requestBuilder.setBoostTerms(query.getBoostTerms());
        }
        try {
			SearchResponse response = requestBuilder.execute().actionGet();
			return parseSearchResponse(clazz, page, size, response);
		} catch (Exception e) {
            logger.error("moreLikeThis Error. ", e);
            throw new SearchException("moreLikeThis Error. typeName="+typeName, e);
		}
	}
		
	// protected
	protected String getTypeNameFromClass(Class<?> clzz) {
		return clzz.getSimpleName();
	}
	
	protected List<String> extractIds(SearchResponse response) {
        List<String> ids = new ArrayList<String>();
        for (SearchHit hit : response.getHits()) {
            if (hit != null) {
                ids.add(hit.getId());
            }
        }
        return ids;
    }

	protected String[] toArray(List<String> values) {
        String[] valuesAsArray = new String[values.size()];
        return values.toArray(valuesAsArray);
    }

	
}
