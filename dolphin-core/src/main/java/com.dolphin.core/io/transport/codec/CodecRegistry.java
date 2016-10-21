package com.dolphin.rpc.core.io.transport.codec;

import java.awt.event.MouseWheelEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * 编码注册器
 * @author jiujie
 * @version $Id: CodecRegistry.java, v 0.1 2016年3月31日 上午11:06:56 jiujie Exp $
 */
public class CodecRegistry {

    private Logger               logger        = Logger.getLogger(CodecRegistry.class);

    private Map<Short, Codec>    codecMappings = new ConcurrentHashMap<>();

    private static CodecRegistry codecRegistry = new CodecRegistry();

    private CodecRegistry() {
        init();
    }

    private void init() {
        ProtobufferCodec protobufferCodec = new ProtobufferCodec();
        codecMappings.put(protobufferCodec.getId(), protobufferCodec);
        JSONCodec jsonCodec = new JSONCodec();
        codecMappings.put(jsonCodec.getId(), jsonCodec);
    }

    public static CodecRegistry getInstance() {
        return codecRegistry;
    }

    /**
     * 注册一个编解码器
     * @author jiujie
     * 2016年3月31日 上午11:46:27
     * @param codec
     */
    public void register(Codec codec) {
        if (codec == null) {
            logger.error("Codec register faild, codec can't be null.");
        }
        if (codecMappings.containsKey(codec.getId())) {
            logger.error("Codec register faild, codec has bean already exists.");
        }
        codecMappings.put(codec.getId(), codec);

    }

    public Codec get(short codecId) {
        return codecMappings.get(codecId);
    }

}
