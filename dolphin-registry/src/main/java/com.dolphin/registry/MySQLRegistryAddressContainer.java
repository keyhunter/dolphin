package com.dolphin.registry;

import com.dolphin.core.config.RegistryConfig;
import com.dolphin.core.exception.AddressExitsException;
import com.dolphin.core.exception.AddressFormatException;
import com.dolphin.core.protocle.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * mysql 实现的 服务地址注册容器
 *
 * @author tianxiao
 * @version $Id: MySQLRegistryAddressContainer.java, v 0.1 2016年7月19日 上午11:13:10 tianxiao Exp $
 */
public class MySQLRegistryAddressContainer implements RegistryAddressContainer {

    private static final Logger logger = LoggerFactory.getLogger(MySQLRegistryAddressContainer.class);

    private static RegistryAddressContainer registryAddressContainer = new MySQLRegistryAddressContainer();

    private String url;

    private String username;

    private String password;

    // 加载驱动
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.info("驱动加载出错!");
        }
    }

    private MySQLRegistryAddressContainer() {
        RegistryConfig registryConfig = RegistryConfig.getInstance();
        url = registryConfig.getDbUrl();
        username = registryConfig.getDbUsername();
        password = registryConfig.getDbPassword();
        try {
            password = SecurityAES.decrypt(password, "yunjee");
        } catch (Exception exception) {
            logger.error("decrypt password error", exception);
        }

    }

    public static RegistryAddressContainer getInstance() {
        return registryAddressContainer;
    }

    /**
     * @see com.dolphin.rpc.registry.RegistryAddressContainer#getAll()
     */
    @Override
    public List<HostAddress> getAll() {
        List<HostAddress> addresses = new ArrayList<>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.createStatement();
            String sql;
            sql = "SELECT host,port FROM registry_server_address";
            rs = st.executeQuery(sql);
            while (rs.next()) {
                HostAddress hostAddress = new HostAddress(rs.getString(1), rs.getInt(2));
                addresses.add(hostAddress);
            }
        } catch (Exception e) {
            logger.error("getAll error", e);
            ;
        } finally {
            free(rs, st, conn);
        }

        return addresses;

    }

    /**
     * 判断当前地址是否存在
     *
     * @param address
     * @return
     * @author tianxiao
     * @version 2016年7月19日 上午11:10:42 tianxiao
     */
    private boolean exist(HostAddress address) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT host,port FROM registry_server_address where host=? and port=?";
            st = conn.prepareStatement(sql);
            st.setString(1, address.getHost());
            st.setInt(2, address.getPort());
            rs = st.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            logger.error("exist error", e);
            ;
        } finally {
            free(rs, st, conn);
        }
        return false;
    }

    /**
     * @see com.dolphin.rpc.registry.RegistryAddressContainer#add(com.dolphin.rpc.core.io.HostAddress)
     */
    @Override
    public void add(HostAddress address) {
        if (!HostAddress.verify(address)) {
            throw new AddressFormatException();
        }
        if (exist(address)) {
            throw new AddressExitsException();
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            String sql;
            sql = "insert into registry_server_address (host,port) values (?,?)";
            st = conn.prepareStatement(sql);
            st.setString(1, address.getHost());
            st.setInt(2, address.getPort());
            st.execute();
        } catch (Exception e) {
            logger.error("add error", e);
        } finally {
            free(null, st, conn);
        }
    }

    /**
     * @see com.dolphin.rpc.registry.RegistryAddressContainer#remove(com.dolphin.rpc.core.io.HostAddress)
     */
    @Override
    public void remove(HostAddress address) {
        if (!HostAddress.verify(address)) {
            throw new AddressFormatException();
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            String sql;
            sql = "delete from registry_server_address where host=? and port=?";
            st = conn.prepareStatement(sql);
            st.setString(1, address.getHost());
            st.setInt(2, address.getPort());
            st.execute();
        } catch (Exception e) {
            logger.error("remove error", e);
        } finally {
            free(null, st, conn);
        }
    }

    // 获得连接
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // 释放连接
    public static void free(ResultSet rs, Statement st, Connection conn) {
        try {
            if (rs != null) {
                rs.close(); // 关闭结果集
            }
        } catch (SQLException e) {
            logger.error("free error", e);
            ;
        } finally {
            try {
                if (st != null) {
                    st.close(); // 关闭Statement
                }
            } catch (SQLException e) {
                logger.error("free error", e);
                ;
            } finally {
                try {
                    if (conn != null) {
                        conn.close(); // 关闭连接
                    }
                } catch (SQLException e) {
                    logger.error("free error", e);
                }

            }

        }

    }

    public static void main(String[] args) {
    }

}
