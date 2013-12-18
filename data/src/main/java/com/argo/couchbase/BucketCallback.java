package com.argo.couchbase;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Defines the callback which will be wrapped and executed on a bucket.
 *
 * @author Michael Nitschinger
 */
public interface BucketCallback<T> {

  /**
   * The enclosed body will be executed on the connected bucket.
   *
   * @return the result of the enclosed execution.
   * @throws TimeoutException if the enclosed operation timed out.
   * @throws ExecutionException if the result could not be retrieved because of a thrown exception before.
   * @throws InterruptedException if the enclosed operation was interrupted.
   */
  T doInBucket() throws TimeoutException, ExecutionException, InterruptedException;

}
