package com.argo.search;

import java.util.Map;

public class SearchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3386446396960525383L;
	
	private Map<String, String> failedDocuments;

	public SearchException(String message) {
		super(message);
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchException(String message, Throwable cause, Map<String, String> failedDocuments) {
		super(message, cause);
		this.failedDocuments = failedDocuments;
	}

	public SearchException(String message, Map<String, String> failedDocuments) {
		super(message);
		this.failedDocuments = failedDocuments;
	}

	public Map<String, String> getFailedDocuments() {
		return failedDocuments;
	}
}
