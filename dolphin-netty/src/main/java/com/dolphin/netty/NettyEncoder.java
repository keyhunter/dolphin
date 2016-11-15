package com.dolphin.netty;

import com.dolphin.core.exception.RPCRunTimeException;
import com.dolphin.core.protocle.Packet;
import com.dolphin.core.protocle.transport.Header;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 包的编码器
 * @author jiujie
 * @version $Id: PacketEncoder.java, v 0.1 2016年3月31日 下午3:04:10 jiujie Exp $
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        if (!(message instanceof Packet)) {
            throw new RPCRunTimeException("Message should instance of this interface Message.");
        }
        Packet msg = (Packet) message;

        byte[] body = msg.getBody();
        out.writeInt(body.length + Header.HEADER_LENGTH);
        Header header = msg.getHeader();
        out.writeShort(header.getVersion());
        out.writeShort(header.getPacketType());
        out.writeBoolean(header.isRequest());
        out.writeBytes(body);
    }

}