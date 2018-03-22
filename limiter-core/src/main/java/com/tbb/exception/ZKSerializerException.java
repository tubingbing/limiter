package com.tbb.exception;

/**
 * 序列化和反序列化数据异常
 * 
 *
 */
public class ZKSerializerException extends RuntimeException {
  private static final long serialVersionUID = -4622853157073346224L;

  public ZKSerializerException() {
    super();
  }

  public ZKSerializerException(String msg) {
    super(msg);
  }

  public ZKSerializerException(Throwable cause) {
    super(cause);
  }

  public ZKSerializerException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
