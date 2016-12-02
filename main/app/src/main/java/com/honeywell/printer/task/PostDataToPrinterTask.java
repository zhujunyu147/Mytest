package com.honeywell.printer.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;

/**
 * Created by zhujunyu on 16/9/27.
 */
public class PostDataToPrinterTask extends AsyncTask<String, Integer, String> {

    private OutputStream mmOutStream;
    private byte[] bytes;
    public ProgressDialog dialog;
    private Context context;
    private long totalFileSize;
    int bytesRead;

    public PostDataToPrinterTask(Context context, OutputStream mmOutStream, byte[] bytes) {
        this.mmOutStream = mmOutStream;
        this.bytes = bytes;
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setMessage("Uploading...");
        dialog.setIndeterminate(false);
        dialog.setTitle("上传图片到打印机中");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
        Log.e("dialog是否显示", "" + dialog.isShowing());
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            int bufferLength = 1024;
            for (int i = 0; i < bytes.length; i += bufferLength) {
                int progress = (int) ((i / (float) bytes.length) * 100);
                publishProgress(progress);
                if (bytes.length - i >= bufferLength) {
                    mmOutStream.write(bytes, i, bufferLength);
                } else {
                    mmOutStream.write(bytes, i, bytes.length - i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BluetoothService", "Exception during write", e);
        }
        publishProgress(100);

//
//        totalFileSize = bytes.length;
//        try {
//            mmOutStream.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("BluetoothService", "Exception during write", e);
//        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.e("当前进度", "" + values[0]);
        dialog.setProgress(values[0]);
    }


    @Override
    protected void onPostExecute(String s) {
        dialog.dismiss();
    }
}
