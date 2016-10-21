package com.dolphin.rpc.core.hash;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性Hash
 * @author jiujie
 * @version $Id: ConsistentHash.java, v 0.1 2016年5月4日 下午5:08:33 jiujie Exp $
 */
public class ConsistentHash<T> {

    /** Hasn方法 @author jiujie 2016年5月4日 下午5:17:53 */
    private final HashFunction       hashFunction;
    //复制的虚拟节点的数量，使整个环更加平衡
    private final int                numberOfReplicas;
    //节点的环
    private final SortedMap<Long, T> circle = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hash(key.toString());
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

}