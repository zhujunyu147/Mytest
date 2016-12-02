package com.honeywell.printer.dom4parse;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.honeywell.printer.activity.HomeActivity;
import com.honeywell.printer.util.FileUtil;
import com.honeywell.printer.service.BluetoothService;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/24.
 */
public class ParseXml {
    private BluetoothService mService = null;
    private Context mContext;
    private int mRequestType;
    private Response mResponse;
    private String message;
    private File mFile;
    private byte[] bytes;

    public ParseXml(Context context, int requestType, String request) {
        this.mContext = context;
        this.mService = HomeActivity.getBluetoothService();
        this.mRequestType = requestType;
        this.message = request;

    }


    public ParseXml(Context context, File file) {
        this.mContext = context;
        this.mService = HomeActivity.getBluetoothService();
        this.mFile = file;
    }



    public ParseXml(Context context, byte[] bytes) {
        this.mContext = context;
        this.mService = HomeActivity.getBluetoothService();
        this.bytes = bytes;
    }


    public interface Response {
        void success(Element element);

        void failed();
    }


    public interface XmlResponse {
        void getXmlData(String stringResult);
    }

    public void sendMessage(Response response) {
        this.mResponse = response;
        Log.e("发送的指令", "" + message);
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }
            mService.write(send, new XmlResponse() {
                @Override
                public void getXmlData(String stringResult) {
                    try {
                        handlerReturnData(stringResult);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    public void sendFileMessage(Response response) {
        this.mResponse = response;
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] bytes = null;
        try {
            int length = (int) mFile.length();
            Log.e("脚本文件的长度",""+length);
            if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
            {
                System.out.println("this file is max ");
                return;
            }
            bytes = new byte[length];
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(mFile));
           Log.e("查看长度是否相同",""+(buf.available() ==mFile.length()));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("脚本byte",""+bytes.length);
        mService.write(bytes, new XmlResponse() {
            @Override
            public void getXmlData(String stringResult) {
                try {
                    handlerReturnData(stringResult);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void sendPicMessage(Response response) {
        this.mResponse = response;
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        mService.writePic(bytes, new XmlResponse() {
            @Override
            public void getXmlData(String stringResult) {
                try {
                    handlerReturnData(stringResult);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });


    }



    public void sendMessageForSave(Response response) {
        this.mResponse = response;
        Log.e("发送的指令", "" + message);
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "蓝牙没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }
            mService.write(send, new XmlResponse() {
                @Override
                public void getXmlData(String stringResult) {
                    try {
                        FileUtil.saveStringToFile(mContext, stringResult);
                        handlerReturnData(stringResult);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void handlerReturnData(String stringResult) throws UnsupportedEncodingException {
        xmlParseData(stringResult);
    }


    //解析xml
    private void xmlParseData(String xml) {
        try {
            Log.e("^^^^^^^^", "" + xml);
            if (xml.contains("Connected")) {
                mResponse.success(null);
            } else {
                Document doc = DocumentHelper.parseText(xml);
                Element element = doc.getRootElement();
                String tag = element.attributeValue("Action");
                if ("PrintQualityWizard".equals(tag)) {
                    Log.e("质量选项卡数目", "" + element.attributeValue("Select"));
                    mResponse.success(element);
                } else {
                    if ("OK".equals(element.attributeValue("Status"))) {
                        mResponse.success(element);
                    } else {
                        mResponse.failed();
                    }
                }

            }


        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public static class RequestType {

        public static final int REQUEST_CLC = 1;
        public static final int REQUEST_CONN = 2;
        public static final int REQUEST_SETLOCALIZATION = 3;
        public static final int REQUEST_SYSTEMINFO = 4;
        public static final int REQUEST_PRINTQUALITYWIZARD = 5;
        public static final int REQUEST_TEMPLATEFILE = 6;
        public static final int REQUEST_GETCONFIGSCHEMA = 7;
        public static final int REQUEST_GETCONFIG = 8;
        public static final int REQUEST_SETCONFIG = 9;
        public static final int REQUEST_GETFILES = 10;


    }


}
