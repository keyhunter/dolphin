package com.dolphin.rpc.core.serializer;

import com.alibaba.fastjson.JSON;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.registry.ServiceInfo;

public class JSONSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T unSerialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }

    public static void main(String[] args) {

        Serializer serializer = new JSONSerializer();
        ServiceInfo object = new ServiceInfo();
        object.setGroup("sdfsdf");
        object.setName("xxx11");
        object.setHostAddress(new HostAddress("xxxx", 232432));
        byte[] serialize = serializer.serialize(object);
        System.out.println(serializer.unSerialize(serialize, ServiceInfo.class));
    }

}
