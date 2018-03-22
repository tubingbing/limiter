package com.tbb.serializer;

import com.tbb.exception.ZKSerializerException;

/**
 * 序列化和反序列化工具接口
 * 
 *
 * @param <T>
 */
public interface ZKSerializer<T> {

  byte[] serialize(T t) throws ZKSerializerException;

  T deserialize(byte[] bytes) throws ZKSerializerException;
}
