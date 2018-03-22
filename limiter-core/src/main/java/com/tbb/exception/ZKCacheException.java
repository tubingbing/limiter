package com.tbb.exception;

/**
 * 异常包装处理，方便定位
 * 
 */
public class ZKCacheException extends RuntimeException {
  private static final long serialVersionUID = -3748104773215060095L;

  public ZKCacheException() {
    super();
  }

  public ZKCacheException(String message) {
    super(message);
  }

  public ZKCacheException(Throwable cause) {
    super(cause);
  }

  public ZKCacheException(String message, Throwable cause) {
    super(message, cause);
  }
}
