package com.tbb.serializer.impl;


import com.tbb.exception.ZKSerializerException;
import com.tbb.serializer.ZKSerializer;

import java.nio.charset.Charset;

/**
 * 字符串序列化
 * 
 *
 */
public class StringZKSerializer implements ZKSerializer<String> {
  private final Charset charset;

  public StringZKSerializer() {
    this(Charset.forName("UTF8"));
  }

  public StringZKSerializer(Charset charset) {
    this.charset = charset;
  }

  @Override
  public byte[] serialize(String str) throws ZKSerializerException {
    return str == null ? null : str.getBytes(charset);
  }

  @Override
  public String deserialize(byte[] bytes) throws ZKSerializerException {
    return (bytes == null ? null : new String(bytes, charset));
  }
}
