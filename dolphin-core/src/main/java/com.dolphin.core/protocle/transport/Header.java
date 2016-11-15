package com.dolphin.core.protocle.transport;

/**
 * Packet的请求头
 * @author jiujie
 * @version $Id: Header.java, v 0.1 2016年5月11日 下午2:24:48 jiujie Exp $
 */
public class Header {

    public final static short HEADER_LENGTH = 5;

    //RPC协议版本，针对不同版本可能有不同的编解码方案
    private short             version;

    /** 请求类型 @author jiujie 2016年5月15日 下午3:58:35 */
    private short             packetType;

    //TODO 加入是否是一个请求，如果是一个请求的话，则可以有返回，被解码成Response
    private boolean           isRequest;

    public Header(short version, PacketType packetType) {
        this.version = version;
        this.packetType = packetType.getValue();
    }

    public Header(PacketType packetType) {
        this.packetType = packetType.getValue();
    }

    public Header(short version, short packetType) {
        this.version = version;
        this.packetType = packetType;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public short getPacketType() {
        return packetType;
    }

    public void setPacketType(short packetType) {
        this.packetType = packetType;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }
}
