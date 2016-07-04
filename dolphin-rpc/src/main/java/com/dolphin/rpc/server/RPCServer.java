package com.dolphin.rpc.server;

import org.apache.log4j.Logger;

import com.dolphin.rpc.core.config.ServiceConfig;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.Message;
import com.dolphin.rpc.core.io.transport.PacketType;
import com.dolphin.rpc.core.io.transport.RPCRequest;
import com.dolphin.rpc.core.io.transport.RPCResult;
import com.dolphin.rpc.core.utils.HostUtil;
import com.dolphin.rpc.netty.server.NettyServer;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.netty.NettyServiceProvider;
import com.dolphin.rpc.registry.provider.ServiceProvider;
import com.dolphin.rpc.registry.zookeeper.ZooKeeperServiceProvider;
import com.dolphin.rpc.server.invocation.spring.SpringInvoker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * RPC服务器
 * @author jiujie
 * @version $Id: RPCServer.java, v 0.1 2016年5月23日 下午2:40:37 jiujie Exp $
 */
public class RPCServer extends NettyServer {

    private static Logger   logger          = Logger.getLogger(RPCServer.class);

    private ServiceProvider serviceProvider = null;

    public RPCServer(int port) {
        super(port);
        registerHandler("rpcInvokeHandler", new RPCInvokeHandler());
        //TODO 从配置文件取出
        serviceProvider = new ZooKeeperServiceProvider();
        ServiceConfig serviceConfig = new ServiceConfig();
        String servicName = serviceConfig.getServiceName();
        logger.info("Register service " + servicName + ".");
        HostAddress address = new HostAddress(HostUtil.getIp(), port);
        serviceProvider.register(new ServiceInfo(serviceConfig.getGroup(), servicName, address));
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 1112;
        }
        new RPCServer(port).startup();
    }

    /**
     * RPC方法调用的Handler，用于解析RPC请求并处理请求的
     * @author jiujie
     * @version $Id: RPCServer.java, v 0.1 2016年5月23日 下午2:39:58 jiujie Exp $
     */
    @Sharable
    private static class RPCInvokeHandler extends SimpleChannelInboundHandler<Message> {

        private static Logger  logger  = Logger.getLogger(RPCInvokeHandler.class.getName());

        private static Invoker invoker = new SpringInvoker();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

            Header header = message.getHeader();
            RPCRequest requset = message.getBody(RPCRequest.class);
            RPCResult response = new RPCResult();
            response.setRequestId(requset.getId());
            if (header != null && header.getPacketType() == PacketType.HEART_BEAT.getValue()) {
                ctx.writeAndFlush(new Message(message.getHeader(), response));
                return;
            }
            if (header != null && header.getPacketType() == PacketType.RPC.getValue()) {
                try {
                    Object invoke = invoker.invoke(requset.getClassName(), requset.getMethodName(),
                        requset.getParamters(), requset.getParamterTypes());
                    response.setResult(invoke);
                } catch (Exception exception) {
                    response.setException(exception);
                }
                ctx.writeAndFlush(new Message(message.getHeader(), response));
            }
        }

    }

}
