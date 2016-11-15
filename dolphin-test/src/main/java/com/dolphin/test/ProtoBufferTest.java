package com.dolphin.test;

import com.dolphin.core.protocle.transport.codec.Codec;
import com.dolphin.core.protocle.transport.codec.ProtobufferCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtoBufferTest {

    public static void main(String[] args) {

        Codec protobufferCodec = new ProtobufferCodec();
        Order order = new Order();
        order.setOrderNumber(1111111111);
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "桔子"));
        products.add(new Product(2, "橙子"));
        products.add(new Product(3, "香蕉"));
        Map<String, List<Product>> activityProducts = new HashMap<>();
        activityProducts.put("活动1", products);
        order.setProducts(products);
        order.setActivityProducts(activityProducts);
        byte[] encode = protobufferCodec.encode(order);
        Order order2 = protobufferCodec.decode(encode, Order.class);
        Obj obj = new Obj();
        obj.setObject(order);
        obj.setClazz(Order.class);
        encode = protobufferCodec.encode(obj);
        Obj o2 = protobufferCodec.decode(encode, Obj.class);

    }
}
