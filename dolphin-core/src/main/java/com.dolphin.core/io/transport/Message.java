package com.dolphin.rpc.core.io.transport;

import com.dolphin.rpc.core.io.Packet;
import com.dolphin.rpc.core.io.transport.codec.Codec;
import com.dolphin.rpc.core.io.transport.codec.CodecRegistry;
import com.dolphin.rpc.core.io.transport.codec.Codecs;

/**
 * 传输的数据信息,Packet的实现
 * @author jiujie
 * @version $Id: Message.java, v 0.1 2016年5月23日 下午4:03:30 jiujie Exp $
 */
public class Message implements Packet {

    private Codec  codec = CodecRegistry.getInstance().get(Codecs.PROTOBUFFER_CODEC);

    private Header header;

    private byte[] body;

    public Message() {
    }

    public Message(Header header) {
        this.header = header;
    }

    public Message(Header header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public Message(Header header, Object body) {
        this.header = header;
        this.body = codec.encode(body);
    }

    @Override
    public Header getHeader() {
        return header;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public <T> T getBody(Class<T> clazz) {
        return codec.decode(body, clazz);
    }

    public void setHeader(Header header) {
        this.header = header;
    }

}
