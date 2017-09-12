package com.dolphin.registry.zookeeper.config;

import com.dolphin.core.protocle.HostAddress;
import com.dolphin.registry.MySQLRegistryAddressContainer;
import com.dolphin.registry.RegistryAddressContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZookeeperConfig {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperConfig.class);

    private static RegistryAddressContainer container = MySQLRegistryAddressContainer.getInstance();

    private final static String IP_HOST_CONNECT_STRING = ":";

    private final static String COMMA = ",";

    public static String getConnectString() {
        List<HostAddress> address = getAddress();
        StringBuilder sb = new StringBuilder();
        if (null == address) {
            return null;
        }
        for (int i = 0; i < address.size(); i++) {
            HostAddress host = address.get(i);
            if (null == host) {
                continue;
            }
            int endIndex = address.size() - 1;
            if (i < endIndex) {
                sb.append(host.getHost() + IP_HOST_CONNECT_STRING + host.getPort() + COMMA);
            } else {
                sb.append(host.getHost() + IP_HOST_CONNECT_STRING + host.getPort());
            }
        }
        return sb.toString();
    }

    private static List<HostAddress> getAddress() {
        return container.getAll();
    }

    public static void main(String[] args) {
        logger.info(ZookeeperConfig.getConnectString());
    }
}
