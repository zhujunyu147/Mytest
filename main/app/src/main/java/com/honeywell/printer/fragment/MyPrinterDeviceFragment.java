package com.honeywell.printer.fragment;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.cube.qrcode.CaptureActivity;
import com.honeywell.printer.R;
import com.honeywell.printer.activity.HomeActivity;
import com.honeywell.printer.activity.PrinterConfigActivity;
import com.honeywell.printer.activity.PrinterSystemInfoActivity;
import com.honeywell.printer.activity.PrinterWifiActivity;
import com.honeywell.printer.adapter.ConnectedPrinterListAdapter;
import com.honeywell.printer.dom4parse.XmlUtil;
import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.service.BluetoothService;
import com.honeywell.printer.task.SwitchPicTask;
import com.honeywell.printer.util.Const;
import com.honeywell.printer.util.FileUtil;
import com.honeywell.printer.util.ImageUtils;
import com.honeywell.printer.util.events.CommondEvent;
import com.honeywell.printer.util.events.ConnectSuccessEvent;
import com.honeywell.printer.util.events.PrinterDateEvent;
import com.honeywell.printer.util.events.PrinterEvent;
import com.honeywell.printer.util.events.SelectPicSuccessEvent;
import com.honeywell.printer.widget.CustomDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;


/**
 * 推荐页面
 *
 * @author zhujunyu
 */
public class MyPrinterDeviceFragment extends MyFragment implements OnItemClickListener, View.OnClickListener {

    private final String IMAGE_TYPE = "image/*";

    private View view;

    private LinearLayout mLinearLayoutBefore;
    private LinearLayout mLinearLayoutLinking;
    private LinearLayout mLinearLayoutLinedSuccess;
    private LinearLayout mLinearLayoutScanSuccess;
    private Button mButton;
    private ListView mListViewLindedDevices;
    private Button mButtonLinking;
    private TextView mTextViewMac;
    private TextView mTextViewLinkedTitle;
    private static final int LINK_BEFORE = 201;
    private static final int LINK_ING = 202;
    private static final int LINK_SUCCESS = 203;
    private static final int LINK_FAILE = 204;
    private static final int SCAN_SUCCESS = 205;
    private String configUploadName;

    private List<BluetoothDevice> mBluetoothDevices;
    protected BluetoothDevice mBluetoothDevice = null;
    //连接成功后的按钮
    private Button mBtnSystemInfo;
    private Button mBtnConfigSearch;
    private Button mConfigSet;
    private Button mBtnKeyReset;
    private Button mBtnConfigSave;
    private Button mBtnConfigSaveLoad;
    private Button mBtnPicPrinter;
    private Button mBtnPrintConfig;
    private Button mBtnPrintWifiConfig;
    private Button mBtnMediumcheck;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.my_printer_fragment, container, false);

        // 缓存的view需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    protected void onVisible(boolean isInit) {

        if (isInit) {
            initView();//初始化控件
            netGetData();//网络访问，获取列表数据
            initData();
        }
    }

    /**
     * 适配器填充listView数据
     */
    private void initView() {
        mLinearLayoutBefore = (LinearLayout) view.findViewById(R.id.line_unLinked);
        mLinearLayoutLinking = (LinearLayout) view.findViewById(R.id.line_scaned);
        mLinearLayoutLinedSuccess = (LinearLayout) view.findViewById(R.id.line_success);
        mLinearLayoutScanSuccess = (LinearLayout) view.findViewById(R.id.line_scan_result);
        mLinearLayoutBefore.setVisibility(View.VISIBLE);
        mLinearLayoutLinking.setVisibility(View.GONE);
        mLinearLayoutLinedSuccess.setVisibility(View.GONE);
        mLinearLayoutScanSuccess.setVisibility(View.GONE);
        mButton = (Button) view.findViewById(R.id.btn_link);
        mButton.setOnClickListener(this);
        mListViewLindedDevices = (ListView) view.findViewById(R.id.linked_printer_listview);
        mTextViewMac = (TextView) view.findViewById(R.id.device_mac);
        mTextViewMac.setOnClickListener(this);
        mTextViewLinkedTitle = (TextView) view.findViewById(R.id.tv_linked_title);
        initLinkSuccessView(view);
    }

    private void initLinkSuccessView(View view) {
        mBtnSystemInfo = (Button) view.findViewById(R.id.btn_system_info);
        mBtnSystemInfo.setOnClickListener(this);
        mBtnConfigSearch = (Button) view.findViewById(R.id.btn_param_search);
        mBtnConfigSearch.setOnClickListener(this);
        mConfigSet = (Button) view.findViewById(R.id.btn_param_setting);
        mConfigSet.setOnClickListener(this);
        mBtnKeyReset = (Button) view.findViewById(R.id.btn_restart);
        mBtnKeyReset.setOnClickListener(this);
        mBtnConfigSave = (Button) view.findViewById(R.id.btn_configuration_save);
        mBtnConfigSave.setOnClickListener(this);
        mBtnConfigSaveLoad = (Button) view.findViewById(R.id.btn_configuration_save_load);
        mBtnConfigSaveLoad.setOnClickListener(this);
        mBtnPicPrinter = (Button) view.findViewById(R.id.btn_print_pic);
        mBtnPicPrinter.setOnClickListener(this);
        mBtnPrintConfig = (Button) view.findViewById(R.id.btn_print_defalt_pic);
        mBtnPrintConfig.setOnClickListener(this);
        mBtnPrintWifiConfig = (Button) view.findViewById(R.id.btn_print_wifi_config);
        mBtnPrintWifiConfig.setOnClickListener(this);
        mBtnMediumcheck = (Button) view.findViewById(R.id.btn_print_Medium_check);
        mBtnMediumcheck.setOnClickListener(this);
    }


    /**
     * /网络访问，获取列表数据
     */
    private void netGetData() {

    }

    private void initData() {
        Set<BluetoothDevice> set = HomeActivity.getBluetoothService().getPairedDev();
        mBluetoothDevices = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : set) {
            if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == 1536) {
                mBluetoothDevices.add(bluetoothDevice);
            }
        }
        if (mBluetoothDevices.size() == 0) {
            mTextViewLinkedTitle.setVisibility(View.GONE);
        } else {
            ConnectedPrinterListAdapter statusListAdapter = new ConnectedPrinterListAdapter(getActivity(), mBluetoothDevices, R.layout.activity_linked_device_item);
            mListViewLindedDevices.setAdapter(statusListAdapter);
            mListViewLindedDevices.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    mBluetoothDevice = mBluetoothDevices.get(position);
                    if (HomeActivity.getBluetoothService().getState() == BluetoothService.STATE_CONNECTED) {
                        if (mBluetoothDevices.size() == 1) {

                        }
                    }
                    showLoadingDialog();
                    if (HomeActivity.getBluetoothService().isAvailable() == false) {
                        Toast.makeText(getActivity(), getString(R.string.bluetooth_unable_toast), Toast.LENGTH_SHORT).show();
                        dismissLoadingDialog();
                    }

                    HomeActivity.getBluetoothService().connect(mBluetoothDevice);
                }
            });
        }

    }

    /**
     * 控件监听事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }


    @Override
    public void onClick(View view) {
        if (view == mButton) {


            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.link_dialog_select_model));
            builder.setTitle(getString(R.string.link_dialog_title));
            builder.setPositiveButton(getString(R.string.link_dialog_wifi), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(), PrinterWifiActivity.class);
                    startActivity(intent);
                    dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.link_dialog_bt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivity(intent);
                    dialogInterface.dismiss();
                }
            });
            builder.createSelectModeDialog().show();
//            Intent intent = new Intent(getActivity(), CaptureActivity.class);
//            startActivity(intent);

        } else if (view == mTextViewMac) {
            showLoadingDialog();
            ((HomeActivity) getActivity()).startConnect(mTextViewMac.getText() + "");
        } else if (view == mBtnSystemInfo) {
            //获取数据，传递给下一个页面
            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.SYSTEM_COMMOND);
        } else if (view == mBtnConfigSearch) {
            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.SEAECH_CONFIG_COMMOND);
        } else if (view == mBtnKeyReset) {
            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.RESTAET_COMMOND);
        } else if (view == mBtnConfigSave) {

            //获取指定路径原始文件名称
            String fileName = FileUtil.getConfigPath(getActivity());

            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.save_dialog_config));
            builder.setTitle(getString(R.string.save_dialog_title));
            builder.setOrignalData(fileName);
            builder.setPositiveButton(getString(R.string.save_dialog_sure), new CustomDialog.Builder.DialogPostiveInterface() {
                @Override
                public void callBack(DialogInterface dialogInterface, String name) {
                    dialogInterface.dismiss();
                    //根据名称，复制一份文件到指定目录
                    FileUtil.copyFile(getActivity(), name);

                }
            });
            builder.setNegativeButton(getString(R.string.save_dialog_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.createSaveConfigDialog().show();

        } else if (view == mBtnConfigSaveLoad) {
            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setMessage(getString(R.string.load_dialog_config));
            builder.setTitle(getString(R.string.save_dialog_title));
            builder.setPositiveButton(getString(R.string.load_dialog_sure), new CustomDialog.Builder.DialogPostiveInterface() {
                @Override
                public void callBack(DialogInterface dialogInterface, String name) {
                    dialogInterface.dismiss();
                    showLoadingDialog();
                    configUploadName = name;
                    XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND);
                }
            });
            builder.setNegativeButton(getString(R.string.load_dialog_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.createSelectConfigDialog().show();


        } else if (view == mBtnPicPrinter) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
            getActivity().startActivityForResult(intent, HomeActivity.REQUEST_IMAGE_CODE);
        } else if (view == mBtnPrintConfig) {

            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.PRINT_CONFIG);
        } else if (view == mBtnPrintWifiConfig) {
            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.PRINT_WIFI_CONFIG);
        } else if (view == mBtnMediumcheck) {
            showLoadingDialog();
            XmlUtil.startConnect(getActivity(), CommondEvent.CommondExcuter.MEDIUM_CHECK);
        }
    }

    private void processPic2(String picPath) {
        new SwitchPicTask(getActivity(), picPath, Const.BLUETOOTH_TYPE).execute();
    }

    public void getFileNameSendCommond(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(getActivity(), "图片损坏", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("fileName", "" + fileName);
        byte[] newByteArray = ImageUtils.image2byte(fileName);
        Log.e("发送给打印机图片的大小", "" + newByteArray.length);
        XmlUtil.picPrinterCommond(getActivity(), CommondEvent.CommondExcuter.PIC_PRINTER, newByteArray);
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

    public void setView(int flag, String result) {

        switch (flag) {
            case LINK_BEFORE:
                mLinearLayoutBefore.setVisibility(View.VISIBLE);
                mLinearLayoutLinking.setVisibility(View.GONE);
                mLinearLayoutLinedSuccess.setVisibility(View.GONE);
                mLinearLayoutScanSuccess.setVisibility(View.GONE);
                break;
            case LINK_ING:
                mLinearLayoutBefore.setVisibility(View.GONE);
                mLinearLayoutLinking.setVisibility(View.VISIBLE);
                mLinearLayoutLinedSuccess.setVisibility(View.GONE);
                mLinearLayoutScanSuccess.setVisibility(View.GONE);
                break;
            case LINK_FAILE:
                mLinearLayoutBefore.setVisibility(View.VISIBLE);
                mLinearLayoutLinking.setVisibility(View.GONE);
                mLinearLayoutLinedSuccess.setVisibility(View.GONE);
                mLinearLayoutScanSuccess.setVisibility(View.GONE);
                break;
            case LINK_SUCCESS:
                mLinearLayoutBefore.setVisibility(View.GONE);
                mLinearLayoutLinking.setVisibility(View.GONE);
                mLinearLayoutLinedSuccess.setVisibility(View.VISIBLE);
                mLinearLayoutScanSuccess.setVisibility(View.GONE);
                break;
            case SCAN_SUCCESS:
                mLinearLayoutBefore.setVisibility(View.GONE);
                mLinearLayoutLinking.setVisibility(View.GONE);
                mLinearLayoutLinedSuccess.setVisibility(View.GONE);
                mLinearLayoutScanSuccess.setVisibility(View.VISIBLE);
                mTextViewMac.setText(result + "");
        }
    }


    public void onEventMainThread(PrinterEvent printerEvent) {

        if (printerEvent instanceof ConnectSuccessEvent) {
            ConnectSuccessEvent connectSuccessEvent = (ConnectSuccessEvent) printerEvent;
            if (connectSuccessEvent.state == ConnectSuccessEvent.ConnectState.CONNECT_SUCCESS) {
                Log.e("eventBus", "连接成功");
                dismissLoadingDialog();
                setView(LINK_SUCCESS, null);
            } else if (connectSuccessEvent.state == ConnectSuccessEvent.ConnectState.CONNECTING) {
                Log.e("eventBus", "连接中");
                setView(LINK_ING, null);
            } else if (connectSuccessEvent.state == ConnectSuccessEvent.ConnectState.CONNECT_FAILE) {
                Log.e("eventBus", "连接失败");
                dismissLoadingDialog();
                setView(LINK_FAILE, null);
            } else if (connectSuccessEvent.state == ConnectSuccessEvent.ConnectState.SCAN_SUCCESS) {
                Log.e("eventBus", "扫描成功");
                String result = connectSuccessEvent.macAddress;
                setView(SCAN_SUCCESS, result);
            }
        } else if (printerEvent instanceof CommondEvent) {
            CommondEvent commondEvent = (CommondEvent) printerEvent;
            if (commondEvent.state == CommondEvent.CommondExcudeState.CLC_SUCCESS) {
                Log.e("eventBus", "CLC_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CLC_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "CLC_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CNN_SUCCESS) {
                Log.e("eventBus", "CNN_SUCCESS");
                if (commondEvent.excuter == CommondEvent.CommondExcuter.SYSTEM_COMMOND) {
                    XmlUtil.getSystemInfoData(((HomeActivity) getActivity()).getBluetoothService(), getActivity(), CommondEvent.CommondExcuter.SYSTEM_COMMOND);
                } else if (commondEvent.excuter == CommondEvent.CommondExcuter.SEAECH_CONFIG_COMMOND) {
                    XmlUtil.searchConfig(((HomeActivity) getActivity()).getBluetoothService(), getActivity(), CommondEvent.CommondExcuter.SEAECH_CONFIG_COMMOND);
                } else if (commondEvent.excuter == CommondEvent.CommondExcuter.RESTAET_COMMOND) {
                    XmlUtil.restartCommond(getActivity(), CommondEvent.CommondExcuter.RESTAET_COMMOND);
                } else if (commondEvent.excuter == CommondEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND) {
                    XmlUtil.uploadConfigCommond(getActivity(), configUploadName, CommondEvent.CommondExcuter.UPLOAD_CONFIG_COMMOND);
                } else if (commondEvent.excuter == CommondEvent.CommondExcuter.PIC_PRINTER) {
//                    XmlUtil.picPrinterCommond(getActivity(), CommondEvent.CommondExcuter.PIC_PRINTER);
                } else if (commondEvent.excuter == CommondEvent.CommondExcuter.PRINT_CONFIG) {
                    XmlUtil.printConfig(getActivity(), CommondEvent.CommondExcuter.PRINT_CONFIG);
                } else  if(commondEvent.excuter == CommondEvent.CommondExcuter.PRINT_WIFI_CONFIG){
                    XmlUtil.printWifiConfig(getActivity(), CommondEvent.CommondExcuter.PRINT_WIFI_CONFIG);
                }else if (commondEvent.excuter == CommondEvent.CommondExcuter.MEDIUM_CHECK) {
                    XmlUtil.mediumCheck(getActivity(), CommondEvent.CommondExcuter.MEDIUM_CHECK);
                }
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.CNN_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "CNN_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.END_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "END_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.END_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "END_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.SYSTEM_INFO_SUCCESS) {
                Log.e("eventBus", "SYSTEM_INFO_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.SYSTEM_INFO_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "SYSTEM_INFO_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.RESTART_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "RESTART_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.RESTART_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "RESTART_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.UPLOAD_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Toast.makeText(getActivity(), "配置信息加载成功", Toast.LENGTH_SHORT).show();
                Log.e("eventBus", "UPLOAD_CONFIG_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.UPLOAD_CONFIG_FAILED) {
                dismissLoadingDialog();
                Toast.makeText(getActivity(), "配置信息加载失败", Toast.LENGTH_SHORT).show();
                Log.e("eventBus", "UPLOAD_CONFIG_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PIC_PRINTE_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "PIC_PRINTE_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PIC_PRINTE_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "PIC_PRINTE_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PRINT_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_CONFIG_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PRINT_CONFIG_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_CONFIG_FAILED");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PRINT_WIFI_CONFIG_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_WIFI_CONFIG_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.PRINT_WIFI_CONFIG_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "PRINT_WIFI_CONFIG_FAILED");
            }else if (commondEvent.state == CommondEvent.CommondExcudeState.MEDIUM_CHECK_SUCCESS) {
                dismissLoadingDialog();
                Log.e("eventBus", "MEDIUM_CHECK_SUCCESS");
            } else if (commondEvent.state == CommondEvent.CommondExcudeState.MEDIUM_CHECK_FAILED) {
                dismissLoadingDialog();
                Log.e("eventBus", "MEDIUM_CHECK_FAILED");
            }

        } else if (printerEvent instanceof PrinterDateEvent) {
            dismissLoadingDialog();
            PrinterDateEvent printerDateEvent = (PrinterDateEvent) printerEvent;
            if (printerDateEvent.dataType == PrinterDateEvent.PrinterDataType.SYSTEM_INFO) {
                List<BaseItem> mBaseItemList = printerDateEvent.mBaseItemList;
                if (mBaseItemList == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), PrinterSystemInfoActivity.class);
                intent.putExtra("status_list", (Serializable) mBaseItemList);
                startActivity(intent);
            } else if (printerDateEvent.dataType == PrinterDateEvent.PrinterDataType.GET_CONFIG) {
                List<BaseItem> mBaseItemList = printerDateEvent.mBaseItemList;
                if (mBaseItemList == null) {
                    return;
                }
                Intent intent = new Intent(getActivity(), PrinterConfigActivity.class);
                intent.putExtra("type", Const.BLUETOOTH_TYPE);
                intent.putExtra("status_list", (Serializable) mBaseItemList);
                startActivity(intent);
            }

        } else if (printerEvent instanceof SelectPicSuccessEvent) {

            SelectPicSuccessEvent selectEvent = (SelectPicSuccessEvent) printerEvent;
            if (selectEvent.selectStatus == SelectPicSuccessEvent.SelectStatus.SELECT_SUCCESS) {
                Log.e("eventBus", "SELECT_SUCCESS");
//                Uri originalUri = (Uri) selectEvent.object;
                String path = (String) selectEvent.object;
//                processPic(path);
                processPic2(path);
            } else if (selectEvent.selectStatus == SelectPicSuccessEvent.SelectStatus.SELECT_FAILED) {
                Log.e("eventBus", "SELECT_FAILED");
            }

        }
    }

}
