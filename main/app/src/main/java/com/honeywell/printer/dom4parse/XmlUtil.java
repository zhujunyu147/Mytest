package com.honeywell.printer.dom4parse;

import com.honeywell.printer.util.FileUtil;
import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.service.BluetoothService;
import com.honeywell.printer.util.events.CommondEvent;
import com.honeywell.printer.util.events.PrinterDateEvent;

import org.dom4j.Element;

import android.content.Context;
import android.util.Log;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujunyu on 16/9/18.
 */
public class XmlUtil {

    public static void startConnect(final Context context, final CommondEvent.CommondExcuter excuter) {
        String message = "{_CLC:HCD}" + "\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_CLC, message);
        parseXml.sendMessage(new ParseXml.Response() {

            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.CLC_SUCCESS, excuter));
                String message = "<DevInfo Action=\"RQ_CONN\">\n" +
                        "</DevInfo>\n";
                ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_CONN, message);
                parseXml.sendMessage(new ParseXml.Response() {

                    @Override
                    public void success(Element element) {
                        CoreXmlParse.parseCnnXml(context, element);
                        EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.CNN_SUCCESS, excuter));
                    }

                    @Override
                    public void failed() {
                        EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.CNN_FAILED, excuter));
                    }
                });
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.CLC_FAILED, excuter));
            }
        });
    }


    private static void endConnect(final Context context, final CommondEvent.CommondExcuter excuter) {
        String message = "<DevInfo Action=\"END_CONN\">\n" +
                "</DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.END_SUCCESS, excuter));
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.END_FAILED, excuter));
            }
        });
    }


    public static void getSystemInfoData(final BluetoothService mService, final Context context, final CommondEvent.CommondExcuter excuter) {
        String message = "<DevInfo Action=\"SystemInfo\"></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {

                List<BaseItem> mBaseItemList = CoreXmlParse.parseSystemXml(element);
                EventBus.getDefault().post(new PrinterDateEvent(mBaseItemList, PrinterDateEvent.PrinterDataType.SYSTEM_INFO));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
            }
        });
    }

    public static void searchConfig(final BluetoothService mService, final Context context, final CommondEvent.CommondExcuter excuter) {
        String message = "<DevInfo Action=\"GetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"></Group></Group></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessageForSave(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                List<BaseItem> mBaseItemList = CoreXmlParse.parseGetConfigXml(element);
                EventBus.getDefault().post(new PrinterDateEvent(mBaseItemList, PrinterDateEvent.PrinterDataType.GET_CONFIG));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {

            }
        });
    }

    public static void setConfig(final Context context, BaseItem baseItem, final CommondEvent.CommondExcuter excuter) {
        String message = "";
        String key = baseItem.key;
        String value = baseItem.value;
        Log.e("修改的key", "" + key);
        if ("Media Type".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Media Type\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Print Method".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Print Method\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Media Margin (X)".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Group Name=\"Print Area\"><Field Name=\"Media Margin (X)\">" + value + "</Field></Group>/Group></Group></Group></DevInfo>\n";
        } else if ("Media Width".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Group Name=\"Print Area\"><Field Name=\"Media Width\">" + value + "</Field></Group>/Group></Group></Group></DevInfo>\n";
        } else if ("Media Length".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Group Name=\"Print Area\"><Field Name=\"Media Length\">" + value + "</Field></Group>/Group></Group></Group></DevInfo>\n";
        } else if ("Clip Default".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Clip Default\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Start Adjust".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Start Adjust\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Stop Adjust".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Stop Adjust\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Media Calibration Mode".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Media Calibration Mode\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Length (Slow Mode)".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Field Name=\"Length (Slow Mode)\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Power Up Action".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Group Name=\"Action\"><Field Name=\"Power Up Action\">" + value + "</Field></Group>/Group></Group></Group></DevInfo>\n";
        } else if ("Head Down Action".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Media\"><Group Name=\"Action\"><Field Name=\"Head Down Action\">" + value + "</Field></Group>/Group></Group></Group></DevInfo>\n";
        } else if ("Print Speed".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Print Quality\"><Field Name=\"Print Speed\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Media Sensitivity".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Print Quality\"><Field Name=\"Media Sensitivity\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Darkness".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Print Quality\"><Field Name=\"Darkness\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        } else if ("Contrast".equals(key)) {
            message = "<DevInfo Action=\"SetConfig\"><Group Name=\"Printer Settings\"><Group Name=\"Printing\"><Group Name=\"Print Quality\"><Field Name=\"Contrast\">" + value + "</Field></Group></Group></Group></DevInfo>\n";
        }


        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.SET_CONFIG_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.SET_CONFIG_FAILED, excuter));
            }
        });
    }


    public static void restartCommond(final Context context, final CommondEvent.CommondExcuter excuter) {
        String message = "<DevInfo Action=\"Restart\"></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.RESTART_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.RESTART_FAILED, excuter));
            }
        });
    }


    public static void uploadConfigCommond(final Context context, String fileName, final CommondEvent.CommondExcuter excuter) {
        String source = FileUtil.getSourceFromFile(context, fileName);
        source = source.replaceAll("<DevInfo Action=\"GetConfig\" Status=\"OK\">", "");
        source = source.replaceAll("</DevInfo>", "");
        Log.e("整理后的字符串", "" + source);
        String message = "<DevInfo Action=\"SetConfig\">" + source + "</DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.UPLOAD_CONFIG_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.UPLOAD_CONFIG_FAILED, excuter));
            }
        });
    }


    public static void picPrinterCommond(final Context context, final CommondEvent.CommondExcuter excuter, byte[] picContent) {


        // CLL/n IMAGE LOAD "PRDOCIMG1.1",5214,""\n
        //PRIMAGE "PRDOCIMG1.1"\nPF 1\n

        int size = picContent.length;
        Log.e("############", "" + size);
        byte[] before = ("CLL\nIMAGE LOAD \"PRDOCIMG1.1\"," + size + ",\"\"\n").getBytes();
//        byte[] before = ("PRBUF" + size + ",\"\"\n").getBytes();
        byte[] body = picContent;
        byte[] after = "\nPRIMAGE \"PRDOCIMG1.1\"\nPF 1\n".getBytes();
//        byte[] after = "\nPF 1\n".getBytes();

        byte[] newByte = new byte[before.length + body.length + after.length];
        System.arraycopy(before, 0, newByte, 0, before.length);
        System.arraycopy(body, 0, newByte, before.length, body.length);
        System.arraycopy(after, 0, newByte, before.length + body.length, after.length);

        ParseXml parseXml = new ParseXml(context, newByte);
        Log.e("############", "" + newByte.length);
        parseXml.sendPicMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {

            }

            @Override
            public void failed() {

            }
        });

//        String message = "CLL\nIMAGE LOAD \"PRDOCIMG1.1\"," + size + ",\"\"\n" + Base64.encodeToString(picContent, Base64.DEFAULT) + "\nPRIMAGE \"PRDOCIMG1.1\"\nPF 1\n";
//        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
//
//        parseXml.sendMessage(new ParseXml.Response() {
//            @Override
//            public void success(Element element) {
//
//            }
//
//            @Override
//            public void failed() {
//
//            }
//        });


//        //获取图片脚本所在的路径
//        File file = FileUtil.getSourceFromPrnFile(context);
//        if (file == null) {
//            return;
//        }
//        ParseXml parseXml = new ParseXml(context, file);
//        parseXml.sendFileMessage(new ParseXml.Response() {
//            @Override
//            public void success(Element element) {
//                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PIC_PRINTE_SUCCESS, excuter));
//                endConnect(context, excuter);
//            }
//
//            @Override
//            public void failed() {
//                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PIC_PRINTE_FAILED, excuter));
//            }
//        });
    }

    public static void printConfig(final Context context, final CommondEvent.CommondExcuter excuter) {

        String message = "<DevInfo Action=\"Testlabel\"><Label>basic_config</Label></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PRINT_CONFIG_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PRINT_CONFIG_FAILED, excuter));
            }
        });
    }

    public static void printWifiConfig(final Context context, final CommondEvent.CommondExcuter excuter) {

        String message = "<DevInfo Action=\"Testlabel\"><Label>hw_wifisetting</Label></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PRINT_CONFIG_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.PRINT_CONFIG_FAILED, excuter));
            }
        });
    }

    public static void mediumCheck(final Context context, final CommondEvent.CommondExcuter excuter) {

        String message = "<DevInfo Action=\"Testfeed\"></DevInfo>\n";
        ParseXml parseXml = new ParseXml(context, ParseXml.RequestType.REQUEST_SYSTEMINFO, message);
        parseXml.sendMessage(new ParseXml.Response() {
            @Override
            public void success(Element element) {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.MEDIUM_CHECK_SUCCESS, excuter));
                endConnect(context, excuter);
            }

            @Override
            public void failed() {
                EventBus.getDefault().post(new CommondEvent(CommondEvent.CommondExcudeState.MEDIUM_CHECK_FAILED, excuter));
            }
        });
    }


}
