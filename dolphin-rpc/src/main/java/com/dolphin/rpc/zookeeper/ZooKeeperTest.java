package com.dolphin.rpc.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperTest implements Watcher {
    private static final int SESSION_TIMEOUT = 5000;

    private ZooKeeper        zk;
    private CountDownLatch   connectedSignal = new CountDownLatch(2);
    Childwatcher             childwatcher    = new Childwatcher();

    private void getthreadname(String funname) {
        Thread current = Thread.currentThread();
        System.out.println(funname + " is call in  " + current.getName());
    }

    @Override
    // 运行在另外一个线程 main-EventThread
    public void process(WatchedEvent event) {
        getthreadname("process");
        System.out.println((event.getType())); // 打印状态

        if (event.getState() == KeeperState.SyncConnected) {
            connectedSignal.countDown();
        }
    }

    // 测试自定义监听
    public Watcher wh = new Watcher() {
        public void process(WatchedEvent event) {
            getthreadname("Watcher::process");
            System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型：" + event.getType());
        }
    };

    public void connect(String hosts) throws IOException, InterruptedException {
        getthreadname("connect");
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, wh); // 最后一个参数用this会调用自身的监听  wh 代表？
        //connectedSignal.await(); // 主线程挂起
    }

    // 加入组(可以理解成一个创建本次连接的一个组)
    public void join(String groupname, String meberName) throws KeeperException,
                                                         InterruptedException {
        String path = "/" + groupname + "/" + meberName;

        // EPHEMERAL断开将被删除
        String createdpath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("join : " + createdpath);
    }

    public List<String> getChilds(String path) throws KeeperException, InterruptedException {
        // TODO Auto-generated method stub 
        if (zk != null) {
            // return zk.getChildren(path, false); // false表示没有设置观察
            //zk.getChildren(path, true, childwatcher.processResult, null);
            zk.getChildren(path, true, new AsyncCallback.Children2Callback() {
                @Override
                public void processResult(int rc, String path, Object ctx, List<String> children,
                                          Stat stat) { // stat参数代表元数据
                    // TODO Auto-generated method stub
                    System.out.println("****");
                    for (int i = 0; i < children.size() - 1; i++) {
                        System.out.println("mempath = " + children.get(i) + stat);
                    }
                }
            }, null);

        }
        return null;
    }

    public void create(String path) throws KeeperException, InterruptedException, IOException {
        // Ids.OPEN_ACL_UNSAFE 开放式ACL，允许任何客户端对znode进行读写
        // CreateMode.PERSISTENT 持久的znode，本次连接断开后还会存在，应该有持久化操作.
        // PERSISTENT_SEQUENTIAL 持久化顺序，这个由zookeeper来保证

        String createdpath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("crateed : " + createdpath);
    }

    private void close() throws InterruptedException {
        if (zk != null) {
            zk.close();
        }
    }

    public static class Childwatcher implements Children2Callback {
        public ChildrenCallback processResult;

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children,
                                  Stat stat) {
            // TODO Auto-generated method stub
            System.out.println("**** path " + stat);
        }
    }

    public void delete(String groupname) throws InterruptedException, KeeperException {
        zk.delete(groupname, -1);
    }

    public Stat isexist(String groupname) throws InterruptedException, KeeperException {
        return zk.exists(groupname, true); // this
    }

    public void write(String path, String value) throws Exception {
        Stat stat = zk.exists(path, false);
        if (stat == null) {
            zk.create(path, value.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {

            zk.setData(path, value.getBytes("UTF-8"), -1);
        }
    }

    public String read(String path, Watcher watch) throws Exception {
        byte[] data = zk.getData(path, watch, null);
        return new String(data, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String hosts = "192.168.0.221:2181,192.168.0.221:2182,192.168.0.221:2183";
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(hosts, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        //        String groupname = "service/cmsService";
        //        String meberName = String.valueOf(System.currentTimeMillis());
        //        String path = "/" + groupname;
        //
        //        // create
        //        ZooKeeperTest test = new ZooKeeperTest();
        //        //  连接
        //        test.connect(hosts);
        //        //
        //        if (null != test.isexist(path)) {
        //            test.delete(path);
        //        }
        //
        //        test.isexist(path);
        //        test.create(path);
        //
        //        test.isexist(path);
        //        test.write(path, "192.168.0.221:");
        //
        //        test.isexist(path);
        //        String result = test.read(path, test.wh);
        //        System.out.println(path + " value = " + result);
        //
        //        int sum = 0;
        //        for (int j = 0; j < 10000; j++) {
        //            sum++;
        //            Thread.sleep(10);
        //        }
        //        System.out.println(sum);
        //        test.close();
        //
        //        System.exit(2);
        //        // 一个本地连接的znode
        //        test.connect(hosts);
        //        test.join(groupname, meberName);
        //
        //        // 遍历
        //        List<String> memlist = test.getChilds("/" + "service");
        //        if (memlist != null) {
        //            for (int i = 0; i < memlist.size() - 1; i++) {
        //                System.out.println("mempath = " + memlist.get(i));
        //            }
        //        }
    }
}