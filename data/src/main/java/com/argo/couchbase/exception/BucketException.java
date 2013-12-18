package com.argo.couchbase.exception;

public class BucketException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7513805609583787254L;

	public BucketException() {
		super();
	}

	public BucketException(String message, Throwable cause) {
		super(message, cause);
	}

	public BucketException(String message) {
		super(message);
	}

	public BucketException(Throwable cause) {
		super(cause);
	}
	
}
