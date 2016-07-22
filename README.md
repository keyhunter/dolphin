# dolphin-rpc
Remote Invocation Framwork
远程调用框架，海豚RPC框架，基于Netty+ProtoBuf+ZooKeeper

特性：
基于Spring架构设计（也支持非Spring架构）
支持Spring注解@RpcResource方式注入远程接口
支持@RPCService注解声明远程服务接口
支持ProtoBuf进行传输中的序列化
基于Netty进行通信，客户端与服务端有心跳检测及重连机制
服务的注册与发现基于ZooKeeper

Spring架构下用法：
客户端通过注解方式注入服务
    @RPCResource
    private UserService userService;
服务端通过Spring的@InitializingBean方式启动
    @Component
    public class RpcBoot implements InitializingBean {
    
        @Override
        public void afterPropertiesSet() throws Exception {
            ServerBoot serverBoot = new ServerBoot();
            serverBoot.start();
        }

    }

非Spring架构下用法：
客户端通过工厂得到服务
    private UserService userService = RPCFactory.getService(UserService.class);

服务端启动
    ServerBoot serverBoot = new ServerBoot();
    serverBoot.start();
