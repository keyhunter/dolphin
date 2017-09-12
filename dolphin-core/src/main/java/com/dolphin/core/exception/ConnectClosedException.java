package com.dolphin.core.exception;

/**
 * 连接关闭异常
 *
 * @author keyhunter
 * @version $Id: ConnectClosedException.java, v 0.1 2016年7月18日 上午11:18:51 keyhunter Exp $
 */
public class ConnectClosedException extends RPCException {

    /**
     * @author keyhunter 2016年7月18日 上午11:18:46
     */
    private static final long serialVersionUID = -1964957276254101544L;

    public ConnectClosedException() {
        super("Connection has been closed.");
    }

}
