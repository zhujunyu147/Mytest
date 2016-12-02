package com.honeywell.printer.dom4parse;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.printer.activity.PrinterWifiActivity;
import com.honeywell.printer.service.BluetoothService;
import com.honeywell.printer.service.SocketService;
import com.honeywell.printer.util.FileUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/24.
 */
public class ParseWifiXml {
    private SocketService mService = null;
    private Context mContext;
    private int mRequestType;
    private Response mResponse;
    private String message;
    private File mFile;
    private byte[] bytes;

    public ParseWifiXml(Context context, int requestType, String request) {
        this.mContext = context;
        this.mService = PrinterWifiActivity.getSocketService();
        this.mRequestType = requestType;
        this.message = request;

    }


    public ParseWifiXml(Context context, File file) {
        this.mContext = context;
        this.mService = PrinterWifiActivity.getSocketService();
        this.mFile = file;
    }


    public ParseWifiXml(Context context, byte[] bytes) {
        this.mContext = context;
        this.mService = PrinterWifiActivity.getSocketService();
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


        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }



            mService.sendData(send, new XmlResponse() {
                @Override
                public void getXmlData(String stringResult) {
                    try {
                        handlerReturnData(stringResult);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });

//            mService.write(send, new ParseXml.XmlResponse() {
//                @Override
//                public void getXmlData(String stringResult) {
//                    try {
//                        handlerReturnData(stringResult);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }

    }

    public void sendPicMessage(ParseWifiXml.Response response) {
        this.mResponse = response;
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(mContext, "WIFI没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        mService.writePic(bytes, new ParseWifiXml.XmlResponse() {
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
            Toast.makeText(mContext, "wifi没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send;
            try {
                send = message.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                send = message.getBytes();
            }

            mService.writePic(send, new XmlResponse() {
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

}
