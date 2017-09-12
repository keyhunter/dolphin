package com.dolphin.core.protocle.transport.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 编码注册器
 *
 * @author keyhunter
 * @version $Id: CodecRegistry.java, v 0.1 2016年3月31日 上午11:06:56 keyhunter Exp $
 */
public class CodecRegistry {

    private Logger logger = LoggerFactory.getLogger(CodecRegistry.class);

    private Map<Short, Codec> codecMappings = new ConcurrentHashMap<>();

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
     *
     * @param codec
     * @author keyhunter
     * 2016年3月31日 上午11:46:27
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
