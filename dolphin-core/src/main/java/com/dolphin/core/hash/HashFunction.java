package com.dolphin.core.hash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hash方法
 *
 * @author keyhunter
 * @version $Id: HashFunction.java, v 0.1 2016年5月4日 下午5:34:45 keyhunter Exp $
 */
public class HashFunction {

    private static final Logger logger = LoggerFactory.getLogger(HashFunction.class);


    public long hash(String key) {

        return MurmurHash.hash32(key);
    }

    public static void main(String[] args) {
        HashFunction hashFunction = new HashFunction();
        logger.info(hashFunction.hash("192.168.1.1") + "");
        logger.info(hashFunction.hash("192.168.1.2") + "");
        logger.info(hashFunction.hash("192.168.1.3") + "");
        logger.info(hashFunction.hash("192.168.1.4") + "");

    }
}
