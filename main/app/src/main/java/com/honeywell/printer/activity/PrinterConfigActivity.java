package com.honeywell.printer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.printer.R;
import com.honeywell.printer.adapter.StatusListAdapter;
import com.honeywell.printer.dom4parse.XmlUtil;
import com.honeywell.printer.dom4parse.XmlWifiUtil;
import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.util.Const;
import com.honeywell.printer.util.events.CommondEvent;
import com.honeywell.printer.util.events.CommondWifiEvent;
import com.honeywell.printer.util.events.PrinterEvent;
import com.honeywell.printer.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujunyu on 16/9/17.
 */
public class PrinterConfigActivity extends BaseCommonActivity {


    private ListView mListView;
    private ImageView mImageViewBack;
    private TextView mTextViewTitle;
    private BaseItem modifyedItem;
    private List<BaseItem> mList;
    private int type;

    @Override
    void setContentView() {
        setContentView(R.layout.activity_status_detail);
    }

    @Override
    void initView() {
        mListView = (ListView) findViewById(R.id.lv);
        mImageViewBack = (ImageView) findViewById(R.id.button_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrinterConfigActivity.this.finish();
            }
        });
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewTitle.setText("打印机参数");

    }

    @Override
    void initEvent() {

    }

    @Override
    void initData() {
        initIntent();
    }

    private void initIntent() {
        Intent intent = getIntent();
        mList = (List<BaseItem>) intent.getSerializableExtra("status_list");
        type = intent.getIntExtra("type", 0);
        StatusListAdapter statusListAdapter = new StatusListAdapter(this, fliterData(mList), R.layout.activity_config_item);
        mListView.setAdapter(statusListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //弹出框，让用户自己修改
                CustomDialog.Builder builder = new CustomDialog.Builder(PrinterConfigActivity.this);
                builder.setMessage("请输入新的参数");
                builder.setTitle("提示");
                builder.setOrignalData(mList.get(i).value);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        modifyedItem = new BaseItem(mList.get(i).key, String.valueOf(which));
                        //设置你的操作事项
                        Log.e("_______修改后的参数", "" + which);
                        Log.e("_______参数的键值对", "" + mList.get(i).key);
                        showLoadingDialog();
                        if(type == Const.BLUETOOTH_TYPE){
                            XmlUtil.startConnect(PrinterConfigActivity.this, CommondEvent.CommondExcuter.SET_CONFIG_COMMOND);
                        }else if(type == Const.WIFI_TYPE){
                            XmlWifiUtil.startConnect(PrinterConfigActivity.this, CommondWifiEvent.CommondExcuter.SET_CONFIG_COMMOND);
                        }

                    }
                });

                builder.setNegativeButton("取消",
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });

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
    }

    private List<BaseItem> fliterData(List<BaseItem> list) {
        List<BaseItem> newList = new ArrayList<>();
        for (BaseItem baseItem : list) {

            if ("Print Speed".equals(baseItem.key)
                    || "Darkness".equals(baseItem.key)
                    || "Media Type".equals(baseItem.key)
                    || "Print Method".equals(baseItem.key)
                    || "Power Up Action".equals(baseItem.key)
                    || "Head Down Action".equals(baseItem.key)
                    || "Media Margin (X)".equals(baseItem.key)
                    || "Media Width".equals(baseItem.key)
                    || "Media Length".equals(baseItem.key)
                    || "Media Calibration Mode".equals(baseItem.key)
                    || "Length (Slow Mode)".equals(baseItem.key)
                    || "Clip Default".equals(baseItem.key)
                    || "Start Adjust".equals(baseItem.key)
                    || "Stop Adjust".equals(baseItem.key)) {
                newList.add(baseItem);
            }
        }
        return newList;
    }

    public void onEventMainThread(PrinterEvent printerEvent) {
        if (printerEvent instanceof CommondEvent) {
            CommondEvent commondEvent = (CommondEvent) printerEvent;
            if (commondEvent.state == CommondEvent.CommondExcudeState.CLC_SUCCESS) {
                Log.e("eventBus", "CLC_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CLC_FAILED) {
                Log.e("eventBus", "CLC_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CNN_SUCCESS) {
                if (commondEvent.excuter == CommondEvent.CommondExcuter.SET_CONFIG_COMMOND) {
                    XmlUtil.setConfig(PrinterConfigActivity.this, modifyedItem, CommondEvent.CommondExcuter.SET_CONFIG_COMMOND);
                }

            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CNN_FAILED) {

            } else if (commondEvent.state == CommondEvent.CommondExcudeState.END_SUCCESS) {
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.END_FAILED) {

            } else if (commondEvent.state == CommondEvent.CommondExcudeState.SET_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Toast.makeText(PrinterConfigActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                //刷新列表，重新获取
                this.finish();
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.SET_CONFIG_FAILED) {
                dismissLoadingDialog();
                Toast.makeText(PrinterConfigActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }

        }else if (printerEvent instanceof CommondWifiEvent) {
            CommondWifiEvent commondEvent = (CommondWifiEvent) printerEvent;
            if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CLC_SUCCESS) {
                Log.e("eventBus", "CLC_SUCCESS");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CLC_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "CLC_FAILED");
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CNN_SUCCESS) {
                if (commondEvent.excuter == CommondWifiEvent.CommondExcuter.SET_CONFIG_COMMOND) {
                    XmlWifiUtil.setConfig(PrinterConfigActivity.this, modifyedItem, CommondWifiEvent.CommondExcuter.SET_CONFIG_COMMOND);
                }
            }else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.CNN_FAILED) {

            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.END_SUCCESS) {
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.END_FAILED) {

            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.SET_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Toast.makeText(PrinterConfigActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                //刷新列表，重新获取
                this.finish();
            } else if (commondEvent.state == CommondWifiEvent.CommondExcudeState.SET_CONFIG_FAILED) {
                dismissLoadingDialog();
                Toast.makeText(PrinterConfigActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
