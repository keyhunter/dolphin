package com.dolphin.core.protocle.transport.codec;

/**
 * 编解码器
 * @author jiujie
 * @version $Id: Codec.java, v 0.1 2016年3月31日 上午11:39:14 jiujie Exp $
 */
public interface Codec {

    /**
     * 编解码器ID，用于标识编解码器
     * @author jiujie
     * 2016年3月31日 上午11:38:39
     * @return
     */
    public short getId();

    /**
     * 把对象数据结构编码成一个DataBuffer
     * @param <T>
     */
    public <T> byte[] encode(T obj);

    /**
     * 把DataBuffer解包构造一个对象
     * @param <T>
     */
    public <T> T decode(byte[] bytes, Class<T> clazz);

}
