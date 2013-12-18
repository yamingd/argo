package com.argo.elasticsearch;


import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class MoreLikeThisQuery {

	private String id;
	private String type;
	private List<String> searchIndices = new ArrayList<String>();
	private List<String> searchTypes = new ArrayList<String>();
	private List<String> fields = new ArrayList<String>();
	private String routing;
	private Float percentTermsToMatch;
	private Integer minTermFreq;
	private Integer maxQueryTerms;
	private List<String> stopWords = new ArrayList<String>();
	private Integer minDocFreq;
	private Integer maxDocFreq;
	private Integer minWordLen;
	private Integer maxWordLen;
	private Float boostTerms;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getSearchIndices() {
		return searchIndices;
	}

	public void addSearchIndices(String... searchIndices) {
        CollectionUtils.addAll(this.searchIndices, searchIndices);
	}

	public List<String> getSearchTypes() {
		return searchTypes;
	}

	public void addSearchTypes(String... searchTypes) {
        CollectionUtils.addAll(this.searchTypes, searchTypes);
	}

	public List<String> getFields() {
		return fields;
	}

	public void addFields(String... fields) {
        CollectionUtils.addAll(this.fields, fields);
	}

	public String getRouting() {
		return routing;
	}

	public void setRouting(String routing) {
		this.routing = routing;
	}

	public Float getPercentTermsToMatch() {
		return percentTermsToMatch;
	}

	public void setPercentTermsToMatch(Float percentTermsToMatch) {
		this.percentTermsToMatch = percentTermsToMatch;
	}

	public Integer getMinTermFreq() {
		return minTermFreq;
	}

	public void setMinTermFreq(Integer minTermFreq) {
		this.minTermFreq = minTermFreq;
	}

	public Integer getMaxQueryTerms() {
		return maxQueryTerms;
	}

	public void setMaxQueryTerms(Integer maxQueryTerms) {
		this.maxQueryTerms = maxQueryTerms;
	}

	public List<String> getStopWords() {
		return stopWords;
	}

	public void addStopWords(String... stopWords) {
        CollectionUtils.addAll(this.stopWords, stopWords);
	}

	public Integer getMinDocFreq() {
		return minDocFreq;
	}

	public void setMinDocFreq(Integer minDocFreq) {
		this.minDocFreq = minDocFreq;
	}

	public Integer getMaxDocFreq() {
		return maxDocFreq;
	}

	public void setMaxDocFreq(Integer maxDocFreq) {
		this.maxDocFreq = maxDocFreq;
	}

	public Integer getMinWordLen() {
		return minWordLen;
	}

	public void setMinWordLen(Integer minWordLen) {
		this.minWordLen = minWordLen;
	}

	public Integer getMaxWordLen() {
		return maxWordLen;
	}

	public void setMaxWordLen(Integer maxWordLen) {
		this.maxWordLen = maxWordLen;
	}

	public Float getBoostTerms() {
		return boostTerms;
	}

	public void setBoostTerms(Float boostTerms) {
		this.boostTerms = boostTerms;
	}

}
