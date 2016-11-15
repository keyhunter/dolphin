package com.dolphin.server.invocation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dolphin.core.invocation.Invoker;
import com.dolphin.core.protocle.transport.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dolphin.core.config.RegistryConfig;
import com.dolphin.core.config.ServiceConfig;
import com.dolphin.core.exception.RPCRunTimeException;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.core.utils.HostUtil;
import com.dolphin.netty.server.NettyServer;
import com.dolphin.registry.provider.ServiceProvider;
import com.dolphin.spring.SpringInvoker;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
        initProvider(port);
    }

    private void initProvider(int port) {
        ServiceConfig serviceConfig = ServiceConfig.getInstance();
        String servicName = serviceConfig.getServiceName();
        //获取当前主机IP
        String currentIp;
        if (StringUtils.isNotBlank(serviceConfig.getIp())) {
            currentIp = serviceConfig.getIp();
        } else {
            currentIp = HostUtil.getIp(serviceConfig.getIpRegex());
        }
        if (StringUtils.isBlank(currentIp)) {
            throw new RPCRunTimeException("Failed to get current service ip.");
        }
        HostAddress address = new HostAddress(currentIp, port);
        ServiceInfo serviceInfo = new ServiceInfo(serviceConfig.getGroup(), servicName, address);
        try {
            serviceProvider = (ServiceProvider) Class
                .forName(RegistryConfig.getInstance().getProvider())
                .getConstructor(ServiceInfo.class).newInstance(serviceInfo);
        } catch (Exception e) {
            logger.error("", e);
            throw new RPCRunTimeException("Init provider failed");
        }
        logger.info("Register service " + servicName + ".");
        serviceProvider.register(serviceInfo);
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

        private static Logger   logger          = Logger
            .getLogger(RPCInvokeHandler.class.getName());

        private static Invoker invoker         = new SpringInvoker();

        //业务执行，要用线程池来执行，不然会阻塞
        private ExecutorService executorService = Executors.newCachedThreadPool();

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
        }

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx,
                                    final Message message) throws Exception {

            Header header = message.getHeader();
            final RPCResult response = new RPCResult();
            if (header != null && header.getPacketType() == PacketType.HEART_BEAT.getValue()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Server heared client heart beat.");
                }
                ctx.writeAndFlush(new Message(message.getHeader(), response));
                return;
            }
            final RPCRequest requset = message.getBody(RPCRequest.class);
            response.setRequestId(requset.getId());
            if (header != null && header.getPacketType() == PacketType.RPC.getValue()) {
                if (ctx.executor().inEventLoop()) {
                    invokeAndReturn(ctx, message, requset, response);
                } else {
                    ctx.executor().execute(new Runnable() {
                        @Override
                        public void run() {
                            invokeAndReturn(ctx, message, requset, response);
                        }
                    });
                }
            }
        }

        private void invokeAndReturn(final ChannelHandlerContext ctx, final Message message,
                                     final RPCRequest requset, final RPCResult response) {
            executorService.execute(new Runnable() {
                public void run() {
                    try {
                        Object result = null;
                        if (StringUtils.isBlank(requset.getImplementName())) {
                            result = invoker.invoke(requset.getClassName(), requset.getMethodName(),
                                requset.getParamters(), requset.getParamterTypes());
                        } else {
                            result = invoker.invoke(requset.getClassName(),
                                requset.getImplementName(), requset.getMethodName(),
                                requset.getParamters(), requset.getParamterTypes());
                        }
                        response.setResult(result);
                    } catch (Exception exception) {
                        logger.error(exception);
                        response.setException(exception);
                    } finally {
                        ctx.writeAndFlush(new Message(message.getHeader(), response));
                    }
                }
            });
        }

    }

}
