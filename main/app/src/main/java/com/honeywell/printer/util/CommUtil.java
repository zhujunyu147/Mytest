package com.honeywell.printer.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.printer.application.SoftApplication;
import com.honeywell.printer.dom4parse.ParseXml;
import com.honeywell.printer.service.BluetoothService;

import java.nio.ByteBuffer;

/**
 * Created by zhujunyu on 16/9/13.
 */
public class CommUtil {

    private static MyHandler mHandler;

    public static boolean checkBlueToothEnable() {
        BluetoothService.getInstance(SoftApplication.getInstance(),mHandler);
        return false;
    }




    public static Handler getInstatnce() {
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        return mHandler;
    }


    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Log.d("蓝牙调试", "连接成功");
                            break;
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d("蓝牙调试", "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d("蓝牙调试", "等待连接.....");
                            break;
                    }

                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Log.d("蓝牙调试", "断开连接.....");
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Log.d("蓝牙调试", "无法连接.....");
                    break;
                case BluetoothService.MESSAGE_READ:
                    ByteBuffer byteBuffer = (ByteBuffer) msg.obj;
                    byte[] bs = byteBuffer.array();
//                    String str = new String(bytes, StandardCharsets.UTF_8);
//                    Log.d("蓝牙调试", "接受到打印机的反馈" + str);
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    Log.d("蓝牙调试", "发送指令给打印机");
                    break;
                case ParseXml.RequestType.REQUEST_SETLOCALIZATION:
                    Toast.makeText(SoftApplication.getInstance(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }


        }
    }
}
