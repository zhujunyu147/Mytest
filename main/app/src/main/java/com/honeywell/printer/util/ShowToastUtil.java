package com.honeywell.printer.util;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/25.
 */
public class ShowToastUtil {
    public static ShowToastUtil sShowToastUtil;
    private static Context mContext;

    public static ShowToastUtil getInstance(Context context) {
        if (sShowToastUtil == null) {
            sShowToastUtil = new ShowToastUtil(context);
        }
        return sShowToastUtil;
    }

    public ShowToastUtil(Context context) {
        mContext = context;
    }

    public void shwoToast(String string) {
        Log.e("是否在住线程", "" + (Looper.myLooper() == Looper.getMainLooper()));
        Message message = new Message();
//        message.what = ParseXml.RequestType.REQUEST_SETLOCALIZATION;
//        message.obj = string;
//        (mContext).getInstatnce().sendMessage(message);
    }
}
