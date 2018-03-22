package com.tbb.serializer.impl;


import com.tbb.exception.ZKSerializerException;
import com.tbb.serializer.ZKSerializer;

import java.io.*;

/**
 * 默认序列化对象
 * 
 *
 */
public class JdkSerializationZKSerializer implements ZKSerializer<Object> {
  private static final byte[] EMPTY_ARRAY = new byte[0];

  public byte[] serialize(Object object) {
    if (object == null) {
      return EMPTY_ARRAY;
    }
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);
    try {
      if (!(object instanceof Serializable)) {
        throw new IllegalArgumentException(
            this.getClass().getSimpleName() + " requires a Serializable payload "
                + "but received an object of type [" + object.getClass().getName() + "]");
      }
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(object);
      objectOutputStream.flush();
      return outputStream.toByteArray();
    } catch (Exception e) {
      throw new ZKSerializerException("Cannot deserialize", e);
    }
  }

  public Object deserialize(byte[] bytes) {
    if ((bytes == null || bytes.length == 0)) {
      return null;
    }
    ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
    try {
      ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
      return objectInputStream.readObject();
    } catch (Exception e) {
      throw new ZKSerializerException("Cannot deserialize", e);
    }
  }
}
