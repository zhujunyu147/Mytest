package com.honeywell.printer.util.events;

/**
 * Created by zhujunyu on 16/9/13.
 */
public class ConnectWifiEvent extends PrinterEvent {


    public enum ConnectWifiState {
        CONNECT_SUCCESS, CONNECT_FAILE, CONNECTING, SCAN_SUCCESS
    }

    public ConnectWifiState state;


    public ConnectWifiEvent(ConnectWifiState state) {
        this.state = state;

    }
}
