package com.honeywell.printer.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujunyu on 16/9/20.
 */
public class FileUtil {


    public static String CONFIG_PATH = "PRINTER-CONFIG";
    public static String CONFIG_SAVE_PATH = "PRINTER-SAVE-CONFIG";

    public static String setFolderPath(Context context, String folderName) {
        String folderPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// save in SD card first
            folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + folderName;
        } else {
            folderPath = context.getFilesDir().getAbsolutePath() + File.separator + folderName + File.separator;
        }
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean ism = folder.mkdirs();
            Log.e("&&&&&&&&&&&&&", "ism:" + ism);
        }
        if (!folder.isDirectory()) {
            Toast.makeText(context, "文件目录错误", Toast.LENGTH_SHORT).show();
            return null;
        }
        return folderPath;
    }

    public static void saveStringToFile(Context context, String source) {
        String path = setFolderPath(context, CONFIG_PATH);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        try {
            File file = new File(path + File.separator + "config.txt");
            if (file.exists()) {
                //存在就删除
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(source.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSourceFromFile(Context context,String fileName) {
        StringBuffer sb = new StringBuffer();
        String path = setFolderPath(context, CONFIG_SAVE_PATH);
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(context, "暂无配置信息", Toast.LENGTH_SHORT).show();
            return "";
        }
        try {
            File file = new File(path + File.separator + fileName);
            if (!file.exists()) {
                //存在就删除
                Toast.makeText(context, "暂无配置信息", Toast.LENGTH_SHORT).show();
                return "";
            } else {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                Log.e("获取config.txt", "" + sb.toString());
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return sb.toString();
    }


    public static String getConfigPath(Context context) {
        String path = setFolderPath(context, CONFIG_PATH);

        File file = new File(path + File.separator + "config.txt");
        if (file.exists()) {
            Log.e("获取文件名称", "" + file.getName());
            return getFileNameNoEx(file.getName());
        }
        return null;
    }

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    public static void copyFile(Context context, String newFileName) {
        String path = setFolderPath(context, CONFIG_PATH);

        String oldPath = path + File.separator + "config.txt";

        String folderPath = setFolderPath(context, CONFIG_SAVE_PATH);
        String newPath = folderPath + File.separator + newFileName + ".txt";

        Log.e("oldPath:", "" + oldPath);
        Log.e("newPath:", "" + newPath);

        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static List<String> getFileNameListFromDir(Context context) {
        List<String> stringList = new ArrayList<>();
        String folderPath = setFolderPath(context, CONFIG_SAVE_PATH);
        File file = new File(folderPath);
        if (file.exists()) {
            File[] files = file.listFiles();// 读取
            for (File fileItem : files) {
                String fileName = fileItem.getName();
                stringList.add(fileName);
            }
        }

        return stringList;
    }


    public static File getSourceFromPrnFile(Context context) {
        StringBuffer sb = new StringBuffer();
        String path = "/storage/emulated/0/printbmp.prn";
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                //存在就删除
                Toast.makeText(context, "没有图片源文件", Toast.LENGTH_SHORT).show();
                return file;
            }
        } catch (Exception e) {
        }
        return file;
    }

}
