package com.tbb.serializer.impl;


import com.tbb.exception.ZKSerializerException;
import com.tbb.serializer.ZKSerializer;

/**
 * boolean序列化类
 * 
 *
 */
public class BooleanZKSerializer implements ZKSerializer<Boolean> {

  @Override
  public byte[] serialize(Boolean b) throws ZKSerializerException {
    if (b == null) {
      throw new ZKSerializerException("BooleanZKSerializer serialize error: " + b);
    }
    byte[] bytes = new byte[1];
    bytes[0] = (byte) (b.booleanValue() ? 1 : 0);
    return bytes;
  }

  @Override
  public Boolean deserialize(byte[] bytes) throws ZKSerializerException {
    if (bytes == null || bytes.length == 0) {
      throw new ZKSerializerException(
          "BooleanZKSerializer deserialize error: " + bytes);
    }
    return bytes[0] != 0;
  }
}
