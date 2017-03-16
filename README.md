# dolphin-rpc
Remote Invocation Framwork，高性能远程调用框架，海豚RPC框架，基于Netty+ProtoBuf+ZooKeeper

## 特性：
* 基于Spring架构设计（也支持非Spring架构）
* 支持Spring注解@RpcResource方式注入远程接口
* 支持@RPCService注解声明远程服务接口
* 支持ProtoBuf进行传输中的序列化
* 基于Netty进行通信，客户端与服务端有心跳检测及重连机制
* 服务的注册与发现基于ZooKeeper

## 使用方法
### 配置文件
#### 客户端配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<dolphin>
	<!-- RPC客户端配置  -->
	<client>
		<global>
			<group>${app.env}</group>
		</global>
		<selector>
			<class>com.dolphin.rpc.proxy.ServiceConnectorSelector</class>
		</selector>
		<services>
			<service>
				<group>debug</group>
				<name>userService</name>
			</service>
		</services>
		<timeout>3000</timeout>
		<retry-times>3</retry-times>
		
	</client>

	<!-- 注册中心配置 -->
	<registry>
		<datasource>
			<url>${registry.db.url}</url>
			<username>${registry.db.username}</username>
			<password>${registry.db.password}</password>
		</datasource>
		<customer>com.dolphin.rpc.registry.zookeeper.ZooKeeperServiceConsumer</customer>
		<provider>com.dolphin.rpc.registry.zookeeper.ZooKeeperServiceProvider</provider>
	</registry>

</dolphin>
```
#### 服务端配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<dolphin>
	<!-- RPC客户端配置  -->
	<client>
		<global>
			<group>${app.env}</group>
		</global>
		<selector>
			<class>com.dolphin.rpc.proxy.ServiceConnectorSelector</class>
		</selector>
		<timeout>3000</timeout>
		<retry-times>3</retry-times>
	</client>

	<!-- RPC服务配置 -->
	<service>
		<group>${app.env}</group>
		<!-- RPC服务名 -->
		<name>userService</name>
		<!-- 主机IP，可不配，程序会自动获取 -->
		<ip>10.1.1.31</ip>
		<!-- RPC服务端口 -->
		<ports>
			<port>${service.port1}</port>
			<port>${service.port2}</port>
		</ports>
	</service>

	<!-- 注册中心配置 -->
	<registry>
		<datasource>
			<url>${registry.db.url}</url>
			<username>${registry.db.username}</username>
			<password>${registry.db.password}</password>
		</datasource>
		<customer>com.dolphin.rpc.registry.zookeeper.ZooKeeperServiceConsumer</customer>
		<provider>com.dolphin.rpc.registry.zookeeper.ZooKeeperServiceProvider</provider>
	</registry>

</dolphin>
```
### Spring架构下用法：
#### 客户端通过注解方式注入服务
```Java
@RPCResource
private UserService userService;
```
#### 服务端通过Spring的@InitializingBean方式启动
```Java
@Component
public class RpcBoot implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBoot serverBoot = new ServerBoot();
        serverBoot.start();
    }
}
```

### 非Spring架构下用法：
#### 客户端通过工厂得到服务
```Java
private UserService userService = RPCFactory.getService(UserService.class);
```
#### 服务端启动
```Java
ServerBoot serverBoot = new ServerBoot();
serverBoot.start();
```
