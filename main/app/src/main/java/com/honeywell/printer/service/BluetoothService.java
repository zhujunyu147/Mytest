package com.honeywell.printer.service;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/1.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


import com.honeywell.printer.activity.HomeActivity;
import com.honeywell.printer.dom4parse.ParseXml;
import com.honeywell.printer.task.PostDataToPrinterTask;
import com.honeywell.printer.util.events.ConnectSuccessEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 200;
    public static final int MESSAGE_WRITE = 300;
    public static final int MESSAGE_DEVICE_NAME = 400;
    public static final int MESSAGE_CONNECTION_LOST = 500;
    public static final int MESSAGE_UNABLE_CONNECT = 600;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler;
    private BluetoothService.AcceptThread mAcceptThread;
    private BluetoothService.ConnectThread mConnectThread;
    private BluetoothService.ConnectedThread mConnectedThread;
    private int mState = 0;
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static BluetoothService sBluetoothService;
    private ParseXml.XmlResponse mResponse;
    private Context context;

    public static BluetoothService getInstance(Context context, Handler handler) {
        if (sBluetoothService == null) {
            sBluetoothService = new BluetoothService(context, handler);
        }
        return sBluetoothService;
    }


    public BluetoothService(Context context, Handler handler) {
        this.mHandler = handler;
        this.context = context;
    }

    public synchronized boolean isAvailable() {
        return this.mAdapter != null;
    }

    public synchronized boolean isBTopen() {
        return this.mAdapter.isEnabled();
    }

    public synchronized BluetoothDevice getDevByMac(String mac) {
        return this.mAdapter.getRemoteDevice(mac);
    }

    //根据名称获取BluetoothDevice
    public synchronized BluetoothDevice getDevByName(String name) {
        BluetoothDevice tem_dev = null;
        Set pairedDevices = this.getPairedDev();
        if (pairedDevices.size() > 0) {
            Iterator var5 = pairedDevices.iterator();

            while (var5.hasNext()) {
                BluetoothDevice device = (BluetoothDevice) var5.next();
                if (device.getName().indexOf(name) != -1) {
                    tem_dev = device;
                    break;
                }
            }
        }

        return tem_dev;
    }

    public synchronized void sendMessage(String message, String charset) {
        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes(charset);
            } catch (UnsupportedEncodingException var5) {
                send = message.getBytes();
            }

            this.write(send);
        }

    }

    public synchronized Set<BluetoothDevice> getPairedDev() {
        Set dev = this.mAdapter.getBondedDevices();
        return dev;
    }

    public synchronized boolean cancelDiscovery() {
        return this.mAdapter.cancelDiscovery();
    }

    public synchronized boolean isDiscovering() {
        return this.mAdapter.isDiscovering();
    }

    public synchronized boolean startDiscovery() {
        return this.mAdapter.startDiscovery();
    }

    private synchronized void setState(int state) {
        this.mState = state;
        this.mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void start() {
        Log.d("BluetoothService", "start");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread == null) {
            this.mAcceptThread = new BluetoothService.AcceptThread();
            this.mAcceptThread.start();
        }

        this.setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d("BluetoothService", "connect to: " + device);
        //正在连
        if (this.mState == STATE_CONNECTING && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        this.mConnectThread = new BluetoothService.ConnectThread(device);
        this.mConnectThread.start();
        this.setState(STATE_CONNECTING);
        EventBus.getDefault().post(new ConnectSuccessEvent(ConnectSuccessEvent.ConnectState.CONNECTING, "", "", null));
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.d("BluetoothService", "connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

        this.mConnectedThread = new BluetoothService.ConnectedThread(socket);
        this.mConnectedThread.start();
        Message msg = this.mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        this.mHandler.sendMessage(msg);
        this.setState(STATE_CONNECTED);
        EventBus.getDefault().post(new ConnectSuccessEvent(ConnectSuccessEvent.ConnectState.CONNECT_SUCCESS, "", "", null));


    }

    public synchronized void stop() {
        Log.d("BluetoothService", "stop");
        this.setState(STATE_NONE);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

    }

    public void write(byte[] out) {
        BluetoothService.ConnectedThread r;
        synchronized (this) {
            Log.e(TAG, "。。。" + this.mState);
            if (this.mState != STATE_CONNECTED) {
                return;
            }

            r = this.mConnectedThread;
        }

        r.write(out);
    }

    public void write(byte[] out, ParseXml.XmlResponse response) {
        BluetoothService.ConnectedThread r;
        this.mResponse = response;
        synchronized (this) {
            Log.e(TAG, "蓝牙状态：" + this.mState);
            if (this.mState != STATE_CONNECTED) {
                return;
            }

            r = this.mConnectedThread;
        }

        r.write(out);
    }

    public void writePic(byte[] out, ParseXml.XmlResponse response) {
        BluetoothService.ConnectedThread r;
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

    private void connectionFailed() {
        this.setState(STATE_LISTEN);
        Message msg = this.mHandler.obtainMessage(MESSAGE_UNABLE_CONNECT);
        this.mHandler.sendMessage(msg);
        EventBus.getDefault().post(new ConnectSuccessEvent(ConnectSuccessEvent.ConnectState.CONNECT_FAILE, "", "", null));
    }

    private void connectionLost() {
        Message msg = this.mHandler.obtainMessage(MESSAGE_CONNECTION_LOST);
        this.mHandler.sendMessage(msg);
        EventBus.getDefault().post(new ConnectSuccessEvent(ConnectSuccessEvent.ConnectState.CONNECT_FAILE, "", "", null));
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = BluetoothService.this.mAdapter.listenUsingRfcommWithServiceRecord("BTPrinter", BluetoothService.MY_UUID);
            } catch (IOException var4) {
                Log.e("BluetoothService", "listen() failed", var4);
            }

            this.mmServerSocket = tmp;
        }

        public void run() {
            this.setName("AcceptThread");
            BluetoothSocket socket = null;

            while (BluetoothService.this.mState != STATE_CONNECTED) {
                try {
                    socket = this.mmServerSocket.accept();
                } catch (IOException var6) {
                    Log.e("BluetoothService", "accept() failed", var6);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (BluetoothService.this.mState) {
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException var4) {
                                    Log.e("BluetoothService", "Could not close unwanted socket", var4);
                                }
                                break;
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                BluetoothService.this.connected(socket, socket.getRemoteDevice());
                        }
                    }
                }
            }
            Log.i("BluetoothService", "END mAcceptThread");
        }

        public void cancel() {
            Log.d("BluetoothService", "cancel " + this);

            try {
                this.mmServerSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of server failed", var2);
            }

        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            Log.e("连接的设备名称", "" + tmp.getRemoteDevice().getName());

            this.mmSocket = tmp;
        }

        public void run() {
            Log.i("BluetoothService", "BEGIN mConnectThread");
            this.setName("ConnectThread");
            BluetoothService.this.mAdapter.cancelDiscovery();

            try {
                this.mmSocket.connect();
            } catch (IOException var5) {

                Log.e("~~~~~~~~~", "" + var5.getLocalizedMessage());
                BluetoothService.this.connectionFailed();
                try {
                    this.mmSocket.close();
                } catch (IOException var3) {
                    Log.e("BluetoothService", "unable to close() socket during connection failure", var3);
                }

                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                BluetoothService.this.mConnectThread = null;
            }

            BluetoothService.this.connected(this.mmSocket, this.mmDevice);
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of connect socket failed", var2);
            }

        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private StringBuffer mStringBuilder;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d("BluetoothService", "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException var6) {
                Log.e("BluetoothService", "temp sockets not created", var6);
            }

            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
            mStringBuilder = new StringBuffer();
        }

        public void run() {
            Log.d("ConnectedThread线程运行", "正在运行......");

            try {
                while (true) {
                    byte[] e = new byte[2046];
                    this.mmInStream.read(e);
                    String s = new String(e, "UTF-8");
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
            } catch (IOException var3) {
                Log.e("BluetoothService", "disconnected", var3);
                BluetoothService.this.connectionLost();
                if (BluetoothService.this.mState != 0) {
                    BluetoothService.this.start();
                }
            }

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

        public void write(final byte[] buffer) {
            Log.e(TAG, "write" + buffer.length);
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BluetoothService", "Exception during write", e);
            }
        }


        public void writePic(final byte[] buffer) {
            Log.e(TAG, "write" + buffer.length);


            FragmentActivity activity = (HomeActivity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new PostDataToPrinterTask(context, mmOutStream, buffer).execute("");
                }
            });
        }


//
//        public byte[] subBytes(byte[] src, int begin, int count) {
//            byte[] bs = new byte[count];
//            for (int i = 0; i < count; i++) {
////                bs[i - begin] = src[i];
//                bs[i] = src[i + begin];
//
//            }
////            System.out.println(Arrays.toString(bs));//字节数组打印
//            printHexString(bs);
//            return bs;
//        }

        public void printHexString(byte[] b) {
            String hex = "";
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                buffer.append(hex);
            }
            System.out.println(buffer.toString());
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException var2) {
                Log.e("BluetoothService", "close() of connect socket failed", var2);
            }

        }
    }
}