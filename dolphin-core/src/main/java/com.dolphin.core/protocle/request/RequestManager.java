package com.dolphin.core.protocle.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.dolphin.core.config.ClientConfig;
import com.dolphin.core.exception.ConnectClosedException;
import com.dolphin.core.exception.InvokeTimeoutException;
import com.dolphin.core.exception.RPCException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.Request;
import com.dolphin.core.protocle.Response;
import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.Message;

public class RequestManager {

    private Logger                   logger         = Logger.getLogger(RequestManager.class);

    private final AtomicLong         atomicLong     = new AtomicLong();

    private Map<Long, RequestFuture> requestFutures = new ConcurrentHashMap<>();

    private static RequestManager    requestManager = new RequestManager();

    /** 默认超时时间  @author jiujie 2016年7月18日 上午11:13:33 */
    private final int                TIME_OUT       = ClientConfig.getInstance().getTimeOut();

    private RequestManager() {
    }

    public static RequestManager getInstance() {
        return requestManager;
        
    }

    /**
     * 同步请求
     * @author jiujie
     * 2016年5月11日 下午9:23:20
     * @param request
     * @return 
     * @throws RPCException 
     */
    public Response sysnRequest(Connection connection, Header header,
                                Request request) throws RPCException {
        if (connection.isClose()) {
            throw new ConnectClosedException();
        }
        request.setId(atomicLong.incrementAndGet());
        RequestFuture requestFuture = new RequestFuture();
        requestFutures.put(request.getId(), requestFuture);
        Message message = new Message(header, request);
        connection.writeAndFlush(message);

        Response response = requestFuture.getResponse(TIME_OUT, TimeUnit.MILLISECONDS);
        if (response == null) {
            requestFutures.remove(request.getId());
            throw new InvokeTimeoutException();
        }
        return response;
    }

    public RequestFuture getRequestFuture(long requestId) {
        return requestFutures.get(requestId);
    }

    public void removeRequestFuture(long requestId) {
        requestFutures.remove(requestId);
    }

}
