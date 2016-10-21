package com.dolphin.rpc.core.hash;

/**
 * Hash方法
 * @author jiujie
 * @version $Id: HashFunction.java, v 0.1 2016年5月4日 下午5:34:45 jiujie Exp $
 */
public class HashFunction {

    public long hash(String key) {

        return MurmurHash.hash32(key);
    }

    public static void main(String[] args) {
        HashFunction hashFunction = new HashFunction();
        System.out.println(hashFunction.hash("192.168.1.1"));
        System.out.println(hashFunction.hash("192.168.1.2"));
        System.out.println(hashFunction.hash("192.168.1.3"));
        System.out.println(hashFunction.hash("192.168.1.4"));

    }
}
