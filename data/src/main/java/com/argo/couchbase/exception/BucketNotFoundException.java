package com.argo.couchbase.exception;

public class BucketNotFoundException extends BucketException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7960942289664303171L;

	public BucketNotFoundException() {
		super();
	}

	public BucketNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BucketNotFoundException(String message) {
		super(message);
	}

	public BucketNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
