package com.honeywell.printer.service;

import android.content.Context;
import android.util.Log;

import com.honeywell.printer.dom4parse.ParseWifiXml;
import com.honeywell.printer.task.PostDataToPrinterTask;
import com.honeywell.printer.util.events.ConnectWifiEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujunyu on 16/10/17.
 */

public class SocketService {

    private static final String TAG = "SocketService";
    private Socket mSocket;
    public int POS_OPEN_NETPORT = 9100;// 0X238c
    public OutputStream mOutputStream;
    public InputStream mInputStream;
    private String ipAddress;
    private int mState = 0;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private ParseWifiXml.XmlResponse mResponse;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Context context;


    public SocketService(Context context,String ipAddress) {
        this.ipAddress = ipAddress;
        this.context = context;
    }


    public void connect() {
        if (this.mConnectThread != null) {
            cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            cancel();
            this.mConnectedThread = null;
        }
        mConnectThread = new ConnectThread();
        mConnectThread.start();
        this.setState(STATE_CONNECTING);

    }

    public void connected() {
        if (this.mConnectThread != null) {
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread = null;
        }

        this.mConnectedThread = new ConnectedThread();
        this.mConnectedThread.start();
        this.setState(STATE_CONNECTED);
    }

    private class ConnectThread extends Thread {

        @Override
        public void run() {
            mSocket = new Socket();
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                //根据IP地址和端口号创建套接字地址
                InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, POS_OPEN_NETPORT);
                mSocket.connect(inetSocketAddress, 5000);
                Log.e("------", "连接成功");
                SocketService.this.mState = STATE_CONNECTED;
                EventBus.getDefault().post(new ConnectWifiEvent(ConnectWifiEvent.ConnectWifiState.CONNECT_SUCCESS));
                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();

            } catch (Exception e) {
                Log.e("------", "连接失败");
                EventBus.getDefault().post(new ConnectWifiEvent(ConnectWifiEvent.ConnectWifiState.CONNECT_FAILE));
                e.printStackTrace();
            }

            synchronized (SocketService.this) {
                SocketService.this.mConnectThread = null;
            }

            SocketService.this.connected();
        }
    }


    private class ConnectedThread extends Thread {
        private StringBuffer mStringBuilder;
        public ConnectedThread() {
            mStringBuilder = new StringBuffer();
        }


        public void writePic(final byte[] buffer) {
            Log.e(TAG, "write" + buffer.length);
            new PostDataToPrinterTask(context, mOutputStream, buffer).execute("");

        }

        @Override
        public void run() {
            Log.d("ConnectedThread线程运行", "正在运行......");
            try {
                while (true) {
                    byte[] bytes = new byte[2046];
                    mInputStream.read(bytes);
                    String s = new String(bytes, "UTF-8");
                    Log.e("=========每次拿到的原始数据============", s);
                    //去除乱码
                    String result = "";
                    String newStr = "";
                    for (int z = 0; z < s.length(); z++) {
                        newStr = s.substring(z, z + 1);
                        //没有汉字
                        int ascii = Integer.valueOf(stringToAscii(newStr));
                        if (ascii < 127 && ascii > 0) {
                            result = result + newStr;
                        }
                    }
                    //把原始内容清空
                    if (result.contains("<DevInfo") && mStringBuilder.length() > 0) {
                        mStringBuilder.delete(0, mStringBuilder.length() - 1);
                        Log.e("清空后的builder1", mStringBuilder.length() + "");
                    }
                    Log.e("=========每次处理后的原始数据============", result);
                    mStringBuilder.append(result);
                    System.out.print("--系统打印-" + mStringBuilder);
                    Log.e("-----------------", mStringBuilder.toString());
                    if (result.startsWith("Connected") || (result.contains("{_CLC:HCD}") && result.contains("Connected"))) {
                        if (mResponse != null) {
                            mResponse.getXmlData(mStringBuilder.toString());
                            mStringBuilder.delete(0, mStringBuilder.length() - 1);
                            Log.e("清空后的builder2", mStringBuilder.length() + "");
                        }
                    }


                    if (result.contains("</DevInfo>")) {
                        if (mResponse != null) {
                            mResponse.getXmlData(mStringBuilder.toString());
                            mStringBuilder.delete(0, mStringBuilder.length() - 1);
                            Log.e("清空后的builder3", mStringBuilder.length() + "");
                        }
                    }
                }
            } catch (Exception e) {

            }

        }
    }

    public synchronized int getState() {
        return this.mState;
    }

    private synchronized void setState(int state) {
        this.mState = state;
    }

    public String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    public void sendData(byte[] out, ParseWifiXml.XmlResponse response) {
        try {
            if (mOutputStream == null) {
                Log.e("------", "发送失败");
                return;
            }
            this.mResponse = response;
            mOutputStream.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void writePic(byte[] out, ParseWifiXml.XmlResponse response) {
       ConnectedThread r;
        this.mResponse = response;
        synchronized (this) {
            Log.e(TAG, "蓝牙状态：" + this.mState);
            if (this.mState != STATE_CONNECTED) {
                return;
            }

            r = this.mConnectedThread;
        }

        r.writePic(out);
    }

    public void cancel() {
        try {
            this.mSocket.close();
        } catch (IOException var2) {
            Log.e("BluetoothService", "close() of connect socket failed", var2);
        }

    }


}
