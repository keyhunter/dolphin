package com.dolphin.core.protocle;

import com.dolphin.core.protocle.request.RequestFuture;
import com.dolphin.core.protocle.request.RequestManager;
import com.dolphin.core.protocle.transport.Message;
import com.dolphin.core.protocle.transport.PacketType;
import com.dolphin.core.protocle.transport.RPCResult;
import com.dolphin.core.protocle.transport.RegistryResponse;

/**
 * 请求的返回数据监听器，服务端返回数据则到些Listener中执行
 * @author jiujie
 * @version $Id: ResponseListener.java, v 0.1 2016年5月12日 下午12:50:57 jiujie Exp $
 */
public class ResponseListener implements MessageReadListener {

    private RequestManager requestManager = RequestManager.getInstance();

    @Override
    public void read(Message message) {
        if (message.getHeader().getPacketType() == PacketType.RPC.getValue()) {
            RPCResult result = message.getBody(RPCResult.class);
            long requestId = result.getRequestId();
            RequestFuture requestFuture = requestManager.getRequestFuture(requestId);
            requestManager.removeRequestFuture(requestId);
            if (requestFuture != null) {
                requestFuture.setResponse(result);
            }
        } else if (message.getHeader().getPacketType() == PacketType.REGISTRY.getValue()) {
            RegistryResponse response = message.getBody(RegistryResponse.class);
            long requestId = response.getRequestId();
            RequestFuture requestFuture = requestManager.getRequestFuture(requestId);
            requestManager.removeRequestFuture(requestId);
            if (requestFuture != null) {
                requestFuture.setResponse(response);
            }
        }
    }

}
