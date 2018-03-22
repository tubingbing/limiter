package com.tbb.exception;

/**
 * 状态不一致时抛出该异常
 * 
 *
 */
public class ZKCacheStateException extends RuntimeException {
  private static final long serialVersionUID = 2685351104944167321L;

  public ZKCacheStateException() {
    super();
  }

  public ZKCacheStateException(String message) {
    super(message);
  }

  public ZKCacheStateException(Throwable cause) {
    super(cause);
  }

  public ZKCacheStateException(String message, Throwable cause) {
    super(message, cause);
  }
}
