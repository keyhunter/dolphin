package com.dolphin.rpc.test.registry;

import java.util.List;

import com.dolphin.rpc.core.ApplicationType;
import com.dolphin.rpc.core.io.Connection;
import com.dolphin.rpc.core.io.HostAddress;
import com.dolphin.rpc.core.io.Response;
import com.dolphin.rpc.core.io.request.RequestManager;
import com.dolphin.rpc.core.io.transport.Header;
import com.dolphin.rpc.core.io.transport.PacketType;
import com.dolphin.rpc.registry.MySQLRegistryAddressContainer;
import com.dolphin.rpc.registry.RegistryAddressContainer;
import com.dolphin.rpc.registry.ServiceInfo;
import com.dolphin.rpc.registry.netty.protocle.Commands;
import com.dolphin.rpc.registry.netty.protocle.RegistryRequest;

public class RegistryConnectorTest {

    public static void main(String[] args) {
        RegistryAddressContainer instance = MySQLRegistryAddressContainer.getInstance();
        List<HostAddress> all = instance.getAll();
        RequestManager requestManager = RequestManager.getInstance();
        for (HostAddress address : all) {
            RegistryConnector connector = new RegistryConnector();
            Connection connection = connector.connect(address);
            Response response = RequestManager.getInstance().sysnRequest(connection,
                new Header(PacketType.REGISTRY), new RegistryRequest(
                    ApplicationType.REGISTRY_SERVER, Commands.SYCN_SERVICE_INFO, null));
            List<ServiceInfo> serviceInfos = (List<ServiceInfo>) response.getResult();
            System.out.println(
                "Print service info:" + address + "-------------size:" + serviceInfos.size());
            if (serviceInfos != null && !serviceInfos.isEmpty()) {
                for (ServiceInfo next : serviceInfos) {
                    System.out.println(next.toString());
                }
            }
            System.out.println("End print service info-------------");
            connector.shutdown();
        }
    }
}
