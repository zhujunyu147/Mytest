package com.honeywell.printer.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.printer.dom4parse.XmlUtil;
import com.honeywell.printer.dom4parse.XmlWifiUtil;
import com.honeywell.printer.util.Const;
import com.honeywell.printer.util.ImageUtils;
import com.honeywell.printer.util.LoadingDialog;
import com.honeywell.printer.util.events.CommondEvent;
import com.honeywell.printer.util.events.CommondWifiEvent;

import java.io.IOException;

/**
 * Created by zhujunyu on 16/9/27.
 */
public class SwitchPicTask extends AsyncTask<String, Integer, String> {

    private String picPath;
    //    private MyPrinterDeviceFragment myPrinterDeviceFragment;
    private LoadingDialog mLoadingDialog;
    private Context context;
    private int type;

    public SwitchPicTask(Context context, String picPath, int type) {
        this.context = context;
        this.picPath = picPath;
        this.type = type;
//        this.myPrinterDeviceFragment = myPrinterDeviceFragment;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showLoadingDialog(context);
    }

    @Override
    protected String doInBackground(String... params) {

        //图片转成byte数组
        byte[] byteArray = ImageUtils.image2byte(picPath);
        Log.e("图片转成bitmap前的大小", "" + byteArray.length);
        //byte数组转成bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.e("图片黑白化前的大小", "" + bitmap.getByteCount());
        //bitmap 黑白
        bitmap = ImageUtils.convertToBlackWhite3(bitmap);
        Log.e("图片黑白化后的大小", "" + bitmap.getByteCount());
        //处理后的bitmap保存为bmp格式的图片
        //String fileName = ImageUtils.saveBmp(context,bitmap);

        String fileName = null;
        try {
            fileName = ImageUtils.save(bitmap, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @Override
    protected void onPostExecute(String strName) {
        super.onPostExecute(strName);
        dismissLoadingDialog();
        getFileNameSendCommond(strName);
//        myPrinterDeviceFragment.getFileNameSendCommond(strName);
    }

    public void showLoadingDialog(Context context) {
        if (null == mLoadingDialog) {
            initLoadingDialog(context);
        }
        mLoadingDialog.show();
    }

    private void initLoadingDialog(Context context) {
        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void getFileNameSendCommond(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(context, "图片损坏", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("fileName", "" + fileName);
        byte[] newByteArray = ImageUtils.image2byte(fileName);
        Log.e("发送给打印机图片的大小", "" + newByteArray.length);
        if (type == Const.BLUETOOTH_TYPE) {
            Log.e("图片发送", "蓝牙");
            XmlUtil.picPrinterCommond(context, CommondEvent.CommondExcuter.PIC_PRINTER, newByteArray);
        } else if (type == Const.WIFI_TYPE) {
            Log.e("图片发送", "WiFi");
            XmlWifiUtil.picPrinterCommond(context, CommondWifiEvent.CommondExcuter.PIC_PRINTER, newByteArray);
        }

    }
}
