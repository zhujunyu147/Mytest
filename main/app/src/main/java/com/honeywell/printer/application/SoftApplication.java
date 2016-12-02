package com.honeywell.printer.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.printer.manager.LogcatFileManager;

import java.io.File;


public class SoftApplication extends Application {
    public static Context applicationContext;
    private static SoftApplication instance;

    public int borderViewPosition;//设置边界的view位置，如果是边界的view则支持左滑切换到底部的菜单


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        startLogcatManager();
    }

    @Override
    public void onTerminate() {
        stopLogcatManager();
        super.onTerminate();
    }
    public static SoftApplication getInstance() {
        return instance;
    }


    public int getBorderViewPosition() {
        return borderViewPosition;
    }

    public void setBorderViewPosition(int borderViewPosition) {
        this.borderViewPosition = borderViewPosition;
    }

    public void startLogcatManager() {

        String folderPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// save in SD card first
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BDT-Logcat";
        } else {// If the SD card does not exist, save in the directory of application.

            folderPath = this.getFilesDir().getAbsolutePath() + File.separator + "BDT-Logcat";
        }
        Toast.makeText(applicationContext, "log存储的地址:" + folderPath, Toast.LENGTH_SHORT).show();
        Log.e("**********************", "folderPath:" + folderPath);
        LogcatFileManager.getInstance().start(folderPath);
    }

    private void stopLogcatManager() {

        LogcatFileManager.getInstance().stop();
    }
}
