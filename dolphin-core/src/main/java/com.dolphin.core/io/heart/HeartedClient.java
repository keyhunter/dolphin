package com.dolphin.rpc.core.io.heart;
//package com.dolphin.rpc.net.heart;
//
//import com.dolphin.rpc.net.Client;
//import com.dolphin.rpc.net.Response;
//import com.dolphin.rpc.net.request.RequestManager;
//import com.dolphin.rpc.net.transport.Header;
//import com.dolphin.rpc.net.transport.Message;
//import com.dolphin.rpc.net.transport.RPCRequest;
//import com.dolphin.rpc.net.transport.PacketType;
//
//public abstract class HeartedClient implements Client, HeartBeatable {
//
//    private Heart             heart;
//
//    private final static long BEAT_INTERVAL  = 5000;
//
//    private RequestManager    requestManager = RequestManager.getInstance();
//
//    public HeartedClient() {
//        this.heart = new Heart(this, 3, 3);
//    }
//
//    public HeartedClient(Heart heart) {
//        this.heart = heart;
//    }
//
//    public Heart getHeart() {
//        return heart;
//    }
//
//    @Override
//    public boolean beat() {
//        try {
//            Response response = requestManager.sysnRequest(getConnection(),
//                new Message(new Header(PacketType.HEART_BEAT), new HeartBeat()));
//            Header header = response.getHeader();
//            if (header != null && header.getRequestType() == PacketType.HEART_BEAT.getValue()) {
//                return true;
//            }
//        } catch (Exception exception) {
//            return false;
//        }
//        return false;
//    }
//
//    @Override
//    public long getBeatInterval() {
//        return BEAT_INTERVAL;
//    }
//
//    @Override
//    public boolean isHealth() {
//        return heart.isHealth();
//    }
//
//    public void start() {
//        execute();
//        start(heart);
//    }
//
//    /**
//     * 启动客户端
//     * @author jiujie
//     * 2016年5月15日 下午3:23:03
//     */
//    protected abstract void execute();
//
//    /**
//     * 启动心脏
//     * @author jiujie
//     * 2016年5月15日 下午5:57:40
//     * @param heart
//     */
//    protected abstract void start(Heart heart);
//
//}
