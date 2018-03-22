package com.tbb.util;

import com.jd.o2o.serializer.ZKSerializer;
import com.jd.o2o.serializer.impl.JdkSerializationZKSerializer;

public class ZKDeserializeUtil {
    private final ZKSerializer<?> defaultSerializer;
    private ZKSerializer valueSerializer;

    private ZKDeserializeUtil() {
        this.defaultSerializer = new JdkSerializationZKSerializer();
    }

    public static final ZKDeserializeUtil getInstance() {
        return ZKDeserializeUtil.SingletonHolder.INSTANCE;
    }

    public Object deserialize(byte[] bytes) {
        if(this.valueSerializer == null) {
            this.valueSerializer = this.defaultSerializer;
        }

        return this.valueSerializer.deserialize(bytes);
    }

    public byte[] serialize(Object object) {
        if(this.valueSerializer == null) {
            this.valueSerializer = this.defaultSerializer;
        }

        return this.valueSerializer.serialize(object);
    }

    public ZKSerializer getValueSerializer() {
        return this.valueSerializer;
    }

    public void setValueSerializer(ZKSerializer valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    private static class SingletonHolder {
        private static final ZKDeserializeUtil INSTANCE = new ZKDeserializeUtil();

        private SingletonHolder() {
        }
    }
}
