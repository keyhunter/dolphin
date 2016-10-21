package com.dolphin.rpc.core.serializer;

public interface Serializer {

    byte[] serialize(Object object);

    <T> T unSerialize(byte[] bytes, Class<T> clazz);

}
