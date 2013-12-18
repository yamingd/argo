package com.argo.couchbase.exception;

public class BucketQueryException extends BucketException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3700042476531827270L;

	public BucketQueryException() {
		super();
	}

	public BucketQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public BucketQueryException(String message) {
		super(message);
	}

	public BucketQueryException(Throwable cause) {
		super(cause);
	}
	
}
