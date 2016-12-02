package com.honeywell.printer.util.events;

/**
 * Created by zhujunyu on 16/9/13.
 */
public class ConnectSuccessEvent extends PrinterEvent{


    public enum ConnectState {
        CONNECT_SUCCESS, CONNECT_FAILE, CONNECTING,SCAN_SUCCESS
    }

    public ConnectState state;
    public byte[] buffer;
    public String deviceName;
    public String macAddress;

    public ConnectSuccessEvent(ConnectState state, String deviceName,String macAddress, byte[] buffer) {
        this.state = state;
        this.deviceName = deviceName;
        this.buffer = buffer;
        this.macAddress = macAddress;
    }
}
