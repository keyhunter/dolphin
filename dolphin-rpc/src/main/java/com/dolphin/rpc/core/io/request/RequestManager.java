package com.dolphin.rpc.core.io.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.dolphin.rpc.core.exception.InvokeTimeoutException;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.Request;
import com.dolphin.rpc.core.io.Response;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.Message;

public class RequestManager {

    private Logger                   logger         = Logger.getLogger(RequestManager.class);

    private final AtomicLong         atomicLong     = new AtomicLong();

    private Map<Long, RequestFuture> requestFutures = new ConcurrentHashMap<>();

    private static RequestManager    requestManager = new RequestManager();

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
     */
    public Response sysnRequest(Connection connection, Header header, Request request) {
        request.setId(atomicLong.incrementAndGet());
        RequestFuture requestFuture = new RequestFuture();
        requestFutures.put(request.getId(), requestFuture);
        Message message = new Message(header, request);
        connection.writeAndFlush(message);
        Response response = requestFuture.getResponse(3, TimeUnit.SECONDS);
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
