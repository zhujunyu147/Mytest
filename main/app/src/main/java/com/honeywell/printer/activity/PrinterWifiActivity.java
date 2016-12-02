package com.honeywell.printer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.honeywell.printer.R;
import com.honeywell.printer.dom4parse.XmlWifiUtil;
import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.service.SocketService;
import com.honeywell.printer.task.SwitchPicTask;
import com.honeywell.printer.util.Const;
import com.honeywell.printer.util.FileUtil;
import com.honeywell.printer.util.events.CommondWifiEvent;
import com.honeywell.printer.util.events.ConnectWifiEvent;
import com.honeywell.printer.util.events.PrinterEvent;
import com.honeywell.printer.util.events.PrinterWifiDateEvent;
import com.honeywell.printer.widget.CustomDialog;

import java.io.Serializable;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by zhujunyu on 16/10/10.
 */
public class PrinterWifiActivity extends BaseCommonActivity implements View.OnClickListener {
    private LinearLayout mLlWifiConnectBefore;
    private LinearLayout mLlWifiConnectSuccess;
    private Button mBtnOpenWifi;
    private EditText mEditAddress;
    private Button mBtnLineWifi;
    private WifiManager wifiManager;

    private String mIpAddress;
    private Button mBtnSystemInfo;
    private Button mBtnConfigSearch;
    private Button mBtnKeyReset;
    private Button mBtnConfigSave;
    private Button mBtnConfigSaveLoad;
    private Button mBtnPicPrinter;
    private Button mBtnPrintConfig;
    private Button mBtnMediumcheck;
    private String configUploadName;

    public static SocketService socketService;

    @Override
    void setContentView() {
        setContentView(R.layout.activity_wifi);
    }

    @Override
    void initView() {
        mLlWifiConnectBefore = (LinearLayout) findViewById(R.id.ll_wifi_connect_before);
        mLlWifiConnectSuccess = (LinearLayout) findViewById(R.id.ll_wifi_connect_success);
        mBtnOpenWifi = (Button) findViewById(R.id.btn_open);
        mBtnOpenWifi.setOnClickListener(this);
        mEditAddress = (EditText) findViewById(R.id.et_ip);
        mBtnLineWifi = (Button) findViewById(R.id.btn_connect);

        mBtnSystemInfo = (Button) findViewById(R.id.btn_system_info);
        mBtnSystemInfo.setOnClickListener(this);
        mBtnConfigSearch = (Button) findViewById(R.id.btn_param_search);
        mBtnConfigSearch.setOnClickListener(this);
        mBtnKeyReset = (Button) findViewById(R.id.btn_restart);
        mBtnKeyReset.setOnClickListener(this);
        mBtnConfigSave = (Button) findViewById(R.id.btn_configuration_save);
        mBtnConfigSave.setOnClickListener(this);
        mBtnConfigSaveLoad = (Button) findViewById(R.id.btn_configuration_save_load);
        mBtnConfigSaveLoad.setOnClickListener(this);
        mBtnPicPrinter = (Button) findViewById(R.id.btn_print_pic);
        mBtnPicPrinter.setOnClickListener(this);
        mBtnPrintConfig = (Button) findViewById(R.id.btn_print_defalt_pic);
        mBtnPrintConfig.setOnClickListener(this);
        mBtnMediumcheck = (Button) findViewById(R.id.btn_print_Medium_check);
        mBtnMediumcheck.setOnClickListener(this);

    }

    @Override
    void initEvent() {
        mBtnLineWifi.setOnClickListener(this);
    }

    @Override
    void initData() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            mBtnOpenWifi.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (socketService != null) {
            socketService.cancel();
        }

    }

    @Override
    public void onClick(View view) {
        if (view == mBtnOpenWifi) {
            wifiManager.setWifiEnabled(true);
        } else if (view == mBtnLineWifi) {
            showLoadingDialog();
            mIpAddress = mEditAddress.getText().toString();
            socketService = new SocketService(this, mIpAddress);
            socketService.connect();

        } else if (view == mBtnSystemInfo) {
            showLoadingDialog();
            XmlWifiUtil.startConnect(this, CommondWifiEvent.CommondExcuter.SYSTEM_COMMOND);
        } else if (view == mBtnConfigSearch) {
            showLoadingDialog();
            XmlWifiUtil.startConnect(this, CommondWifiEvent.CommondExcuter.SEAECH_CONFIG_COMMOND);
        } else if (view == mBtnKeyReset) {
            showLoadingDialog();
            XmlWifiUtil.startConnect(this, CommondWifiEvent.CommondExcuter.RESTAET_COMMOND);
        } else if (view == mBtnConfigSave) {

            //获取指定路径原始文件名称
            String fileName = FileUtil.getConfigPath(this);

            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setMessage("保存当前的参数配置");
            builder.setTitle("提示");
            builder.setOrignalData(fileName);
            builder.setPositiveButton("确定", new CustomDialog.Builder.DialogPostiveInterface() {
                @Override
                public void callBack(DialogInterface dialogInterface, String name) {
                    dialogInterface.dismiss();
                    //根据名称，复制一份文件到指定目录
                    FileUtil.copyFile(PrinterWifiActivity.this, name);

                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.createSaveConfigDialog().show();

        } else if (view == mBtnConfigSaveLoad) {
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setMessage("请选择您要加载的配置文件");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new CustomDialog.Builder.DialogPostiveInterface() {
                @Override
                public void callBack(DialogInterface dialogInterface, String name) {
                    dialogInterface.dismiss();
                    showLoadingDialog();
                    configUploadName = name;
                    XmlWifiUtil.startConnect(PrinterWifiActivity.this, CommondWifiEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.createSelectConfigDialog().show();


        } else if (view == mBtnPicPrinter) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
            startActivityForResult(intent, HomeActivity.REQUEST_IMAGE_WIFI_CODE);
        } else if (view == mBtnPrintConfig) {

            showLoadingDialog();
            XmlWifiUtil.startConnect(this, CommondWifiEvent.CommondExcuter.PRINT_CONFIG);
        } else if (view == mBtnMediumcheck) {
            showLoadingDialog();
            XmlWifiUtil.startConnect(this, CommondWifiEvent.CommondExcuter.MEDIUM_CHECK);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            CustomDialog.Builder builder = new CustomDialog.Builder(this);
            builder.setMessage("退出WIFI连接模式？");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.createDefaultDialog().show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static SocketService getSocketService() {
        return socketService;
    }

    private void processPic2(String picPath) {
        new SwitchPicTask(this, picPath, Const.WIFI_TYPE).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HomeActivity.REQUEST_IMAGE_WIFI_CODE:
                Uri originalUri = data.getData();
                String path = null;
                Cursor cursor = getContentResolver().query(originalUri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(1);
                    Log.e("path", "" + path);
                }
                cursor.close();
                processPic2(path);
                break;
        }
    }

    public void onEventMainThread(PrinterEvent printerEvent) {

        if (printerEvent instanceof ConnectWifiEvent) {
            dismissLoadingDialog();
            ConnectWifiEvent connectWifiEvent = (ConnectWifiEvent) printerEvent;
            if (connectWifiEvent.state == ConnectWifiEvent.ConnectWifiState.CONNECT_SUCCESS) {
                mLlWifiConnectBefore.setVisibility(View.GONE);
                mLlWifiConnectSuccess.setVisibility(View.VISIBLE);
            } else if (connectWifiEvent.state == ConnectWifiEvent.ConnectWifiState.CONNECTING) {

            } else if (connectWifiEvent.state == ConnectWifiEvent.ConnectWifiState.CONNECT_FAILE) {
                mLlWifiConnectBefore.setVisibility(View.VISIBLE);
                mLlWifiConnectSuccess.setVisibility(View.GONE);
            }
        } else if (printerEvent instanceof PrinterWifiDateEvent) {
            dismissLoadingDialog();
            PrinterWifiDateEvent printerDateEvent = (PrinterWifiDateEvent) printerEvent;
            if (printerDateEvent.dataType == PrinterWifiDateEvent.PrinterDataType.SYSTEM_INFO) {
                List<BaseItem> mBaseItemList = printerDateEvent.mBaseItemList;
                if (mBaseItemList == null) {
                    return;
                }
                Intent intent = new Intent(this, PrinterSystemInfoActivity.class);
                intent.putExtra("status_list", (Serializable) mBaseItemList);
                startActivity(intent);
            } else if (printerDateEvent.dataType == PrinterWifiDateEvent.PrinterDataType.GET_CONFIG) {
                List<BaseItem> mBaseItemList = printerDateEvent.mBaseItemList;
                if (mBaseItemList == null) {
                    return;
                }
                Intent intent = new Intent(this, PrinterConfigActivity.class);
                intent.putExtra("status_list", (Serializable) mBaseItemList);
                intent.putExtra("type", Const.WIFI_TYPE);
                startActivity(intent);
            }

        } else if (printerEvent instanceof CommondWifiEvent) {
            CommondWifiEvent commondEvent = (CommondWifiEvent) printerEvent;
            if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CLC_SUCCESS) {
                Log.e("eventBus", "CLC_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CLC_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "CLC_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CNN_SUCCESS) {
                Log.e("eventBus", "CNN_SUCCESS");
                if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.SYSTEM_COMMOND) {

                    XmlWifiUtil.getSystemInfoData(PrinterWifiActivity.this, CommondWifiEvent.CommondExcuter.SYSTEM_COMMOND);

                } else if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.SEAECH_CONFIG_COMMOND) {

                    XmlWifiUtil.searchConfig(this, CommondWifiEvent.CommondExcuter.SEAECH_CONFIG_COMMOND);


                } else if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.RESTAET_COMMOND) {

                    XmlWifiUtil.restartCommond(this, CommondWifiEvent.CommondExcuter.RESTAET_COMMOND);

                } else if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND) {

                    XmlWifiUtil.uploadConfigCommond(this, configUploadName, CommondWifiEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND);

                } else if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.PRINT_CONFIG) {

                    XmlWifiUtil.printConfig(this, CommondWifiEvent.CommondExcuter.PRINT_CONFIG);

                } else if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.MEDIUM_CHECK) {

                    XmlWifiUtil.mediumCheck(this, CommondWifiEvent.CommondExcuter.MEDIUM_CHECK);

                }
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CNN_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "CNN_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.END_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "END_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.END_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "END_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.SYSTEM_INFO_SUCCESS) {
                Log.e("eventBus", "SYSTEM_INFO_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.SYSTEM_INFO_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "SYSTEM_INFO_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.RESTART_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "RESTART_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.RESTART_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "RESTART_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.UPLOAD_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Toast.makeText(this, "配置信息加载成功", Toast.LENGTH_SHORT).show();
                Log.e("eventBus", "UPLOAD_CONFIG_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.UPLOAD_CONFIG_FAILED) {
                dismissLoadingDialog();
                Toast.makeText(this, "配置信息加载失败", Toast.LENGTH_SHORT).show();
                Log.e("eventBus", "UPLOAD_CONFIG_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.PIC_PRINTE_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "PIC_PRINTE_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.PIC_PRINTE_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "PIC_PRINTE_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.PRINT_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_CONFIG_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.PRINT_CONFIG_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_CONFIG_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.MEDIUM_CHECK_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "MEDIUM_CHECK_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.MEDIUM_CHECK_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "MEDIUM_CHECK_FAILED");
            }
        }
    }
}
