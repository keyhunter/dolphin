package com.dolphin.netty;

import java.util.List;

import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyDecoder extends ByteToMessageDecoder {

    private final static int INT_BYTE_LENGTH = 4;

    public NettyDecoder() {
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in,
                             List<Object> out) throws Exception {
        //检测输入byteBuffer，避免分包粘包
        if (in.readableBytes() < INT_BYTE_LENGTH) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        Header header = readHeader(in);
        byte[] body = new byte[dataLength - Header.HEADER_LENGTH];
        in.readBytes(body);
        Message message = new Message(header, body);
        out.add(message);
    }

    private Header readHeader(ByteBuf in) {
        short version = in.readShort();
        short packetType = in.readShort();
        boolean isRequest = in.readBoolean();
        Header header = new Header(version, packetType);
        header.setRequest(isRequest);
        return header;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        super.exceptionCaught(ctx, cause);
    }
}