package com.dolphin.rpc.core.io.transport.codec;

import com.alibaba.fastjson.JSON;

/**
 * JSON序列化类，把类序列化成JSON字符串的字节数组
 * @author jiujie
 * @version $Id: JSONSerializable.java, v 0.1 2016年3月31日 上午10:53:16 jiujie Exp $
 */
public class JSONCodec implements Codec {

    @Override
    public short getId() {
        return Codecs.JSON_CODEC;
    }

    /**
     * 把对象数据结构编码成一个DataBuffer
     * @author jiujie
     * 2016年3月31日 下午2:41:19
     * @param <T>
     * @see com.dolphin.rpc.core.io.transport.codec.net.transport.codec.fish.chat.core.codec.Codec#encode()
     * @return 
     */
    public byte[] encode(Object obj) {
        String jsonString = JSON.toJSONString(obj);
        return jsonString.getBytes();
    }

    /**
     * 把DataBuffer解包构造一个对象
     * @author jiujie
     * 2016年3月31日 下午2:41:07
     * @see com.dolphin.rpc.core.io.transport.codec.net.transport.codec.fish.chat.core.codec.Codec#decode(com.fish.chat.core.codec.DataBuffer, java.lang.Class)
     * @return 
     */
    @Override
    public <T> T decode(byte[] buffer, Class<T> clazz) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        String body = new String(buffer);
        return JSON.parseObject(body, clazz);
    }

}
