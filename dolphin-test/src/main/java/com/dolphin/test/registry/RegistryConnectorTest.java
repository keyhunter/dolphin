package com.dolphin.test.registry;

import java.util.List;

import com.dolphin.core.ApplicationType;
import com.dolphin.core.exception.RPCException;
import com.dolphin.core.protocle.Connection;
import com.dolphin.core.protocle.HostAddress;
import com.dolphin.core.protocle.Response;
import com.dolphin.core.protocle.request.RequestManager;
import com.dolphin.core.protocle.transport.Header;
import com.dolphin.core.protocle.transport.PacketType;
import com.dolphin.core.protocle.transport.ServiceInfo;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.RegistryAddressContainer;
import com.dolphin.registry.netty.protocle.Commands;
import com.dolphin.registry.netty.protocle.RegistryRequest;

public class RegistryConnectorTest {

    public static void main(String[] args) throws RPCException {
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
