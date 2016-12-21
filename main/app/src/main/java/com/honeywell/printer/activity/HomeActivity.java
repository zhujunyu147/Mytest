package com.honeywell.printer.activity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.honeywell.printer.R;
import com.honeywell.printer.application.SoftApplication;
import com.honeywell.printer.dom4parse.ParseXml;
import com.honeywell.printer.entity.TabMode;
import com.honeywell.printer.fragment.InfomationFragment;
import com.honeywell.printer.fragment.HomeFragment;
import com.honeywell.printer.fragment.SearchPrinterFragment;
import com.honeywell.printer.libs.DragLayout;
import com.honeywell.printer.libs.MyRelativeLayout;
import com.honeywell.printer.service.BluetoothService;
import com.honeywell.printer.tabhost.TabFragment;
import com.honeywell.printer.util.events.ConnectSuccessEvent;
import com.honeywell.printer.util.events.SelectPicSuccessEvent;

import de.greenrobot.event.EventBus;

/**
 * 主页
 *
 * @author zhujunyu
 */
public class HomeActivity extends BaseActivity {

    public static final int HOME_TAB = 1000;
    public static final int SEARCH_TAB = 2000;
    public static final int HOME_TAB_MESSAGE = 3000;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_ENABLE_Capture = 3;
    public static final int REQUEST_IMAGE_CODE = 4;
    public static final int REQUEST_IMAGE_WIFI_CODE = 5;

    private Context context;
    /**
     * 左边侧滑菜单
     */
    private DragLayout mDragLayout;
    private LinearLayout menu_header;
    private ListView menuListView;// 菜单列表
    private TabFragment actionBarFragment;
    private MyRelativeLayout myRelativeLayout;

    private HomeFragment mHomeFragment;
    private SearchPrinterFragment mSearchPrinterFragment;
    private InfomationFragment mInfomationFragment;


    public String TAG = "printer_demo";
    public static BluetoothService mService = null;
    protected BluetoothDevice mBluetoothDevice = null;
    private List<BluetoothDevice> mBluetoothDevices;
    private static MyHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();// 初始化控件
        initService();

        // 左侧页面listView添加数据
        List<Map<String, Object>> data = getMenuData();

        menuListView.setAdapter(new SimpleAdapter(this, data,
                R.layout.wxtong_leftitem, new String[]{"item", "image", "iv"},
                new int[]{R.id.tv_item, R.id.iv_item, R.id.iv}));
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(HomeActivity.this, AboutHoneywellActivity.class);
                    startActivity(intent);
                } else if (i == 1) {
                    Toast.makeText(HomeActivity.this, HomeActivity.this.getResources().getString(R.string.no_update_toast), Toast.LENGTH_LONG).show();
                }
            }
        });

        checkBluetooth();
    }


    /**
     * 控件初始化
     */
    private void initView() {
        context = this;
        MangerActivitys.addActivity(context);


        // 点击back按钮
        actionBarFragment = (TabFragment) getSupportFragmentManager().findFragmentById(R.id.tab_bar_fragment);

        int code = 1;
        final ArrayList<TabMode> listTabModes = new ArrayList<TabMode>();
        {// 首页
            mHomeFragment = new HomeFragment();
            final TabMode tabMode = new TabMode(HOME_TAB, R.drawable.tab_1_selector,
                    this.getResources().getString(R.string.tv_bottom1), R.color.tab_text_color_selector, mHomeFragment, code == 1);
            listTabModes.add(tabMode);
        }

        {// 搜索
            mSearchPrinterFragment = new SearchPrinterFragment();
            final TabMode tabMode = new TabMode(SEARCH_TAB, R.drawable.tab_2_selector,
                    this.getResources().getString(R.string.tv_bottom2), R.color.tab_text_color_selector, mSearchPrinterFragment, code == 2);//
            listTabModes.add(tabMode);

        }

        {// 消息
            mInfomationFragment = new InfomationFragment();
            final TabMode tabMode = new TabMode(HOME_TAB_MESSAGE, R.drawable.tab_3_selector,
                    this.getResources().getString(R.string.tv_bottom3), R.color.tab_text_color_selector, mInfomationFragment, code == 3);
            listTabModes.add(tabMode);
        }


        actionBarFragment.creatTab(HomeActivity.this, listTabModes, new TabFragment.IFocusChangeListener() {

            @Override
            public void OnFocusChange(int currentTabId, int tabIndex) {

            }
        });


        //这部分是底部menu的view控件
        menu_header = (LinearLayout) this.findViewById(R.id.menu_header);
        mDragLayout = (DragLayout) findViewById(R.id.dl);


        myRelativeLayout = (MyRelativeLayout) findViewById(R.id.rl_layout);
        mDragLayout.setBorder(actionBarFragment);
        myRelativeLayout.setDragLayout(mDragLayout);


        /**
         * 抽屜动作监听
         */
        mDragLayout.setOnLayoutDragingListener(new DragLayout.OnLayoutDragingListener() {

            @Override
            public void onOpen() {
                //打开
            }

            @Override
            public void onDraging(float percent) {
                //滑动中
            }

            @Override
            public void onClose() {
                //关闭
            }
        });

        menuListView = (ListView) findViewById(R.id.menu_listview);// 抽屉列表
        initResideListener();// 个人中心、设置点击事件
    }

    /**
     * 个人中心、设置点击事件
     */
    private void initResideListener() {
        // 点击个人中心
        menu_header.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 加载抽屉列表数据
     *
     * @return
     */
    private List<Map<String, Object>> getMenuData() {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> item;

        item = new HashMap<String, Object>();
        item.put("item", this.getResources().getString(R.string.drag_item_about_Hw));
        item.put("image", R.drawable.ic_launcher);
        item.put("iv", R.drawable.icon_kehu_arrow);
        data.add(item);

        item = new HashMap<String, Object>();
        item.put("item", this.getResources().getString(R.string.drag_item_update));
        item.put("image", R.drawable.ic_launcher);
        item.put("iv", R.drawable.icon_kehu_arrow);
        data.add(item);

        return data;
    }


    /**
     * activity对象回收
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < MangerActivitys.activitys.size(); i++) {
            if (MangerActivitys.activitys.get(i) != null) {
                ((Activity) MangerActivitys.activitys.get(i)).finish();
            }
        }
        finish();
        System.gc();
    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(HomeActivity.this.getResources().getString(R.string.exit_dialog_title))
                    .setCancelable(false)
                    .setPositiveButton(HomeActivity.this.getResources().getString(R.string.exit_dialog_sure),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    HomeActivity.this.finish();
                                    System.exit(0);
//                                    Intent intent = new Intent(
//                                            Intent.ACTION_MAIN);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.addCategory(Intent.CATEGORY_HOME);
//                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton(HomeActivity.this.getResources().getString(R.string.exit_dialog_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    void initService() {

        mService = BluetoothService.getInstance(this, getInstatnce());
    }

    public static Handler getInstatnce() {
        if (mHandler == null) {
            mHandler = new MyHandler();
        }
        return mHandler;
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Log.d("蓝牙调试", "连接成功");
                            break;
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d("蓝牙调试", "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d("蓝牙调试", "等待连接.....");
                            break;
                    }

                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Log.d("蓝牙调试", "断开连接.....");
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Log.d("蓝牙调试", "无法连接.....");
                    break;
                case BluetoothService.MESSAGE_READ:
                    ByteBuffer byteBuffer = (ByteBuffer) msg.obj;
                    byte[] bs = byteBuffer.array();
//                    String str = new String(bytes, StandardCharsets.UTF_8);
//                    Log.d("蓝牙调试", "接受到打印机的反馈" + str);
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    Log.d("蓝牙调试", "发送指令给打印机");
                    break;
                case ParseXml.RequestType.REQUEST_SETLOCALIZATION:
                    Toast.makeText(SoftApplication.getInstance(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }


        }
    }

    public static BluetoothService getBluetoothService() {
        return mService;
    }

    //检测设备蓝牙是否开启
    private void checkBluetooth() {

        if (mService.isBTopen() == false) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {   //蓝牙已经打开
                    Toast.makeText(SoftApplication.getInstance(), "Bluetooth open successful", Toast.LENGTH_LONG).show();
                } else {                 //用户不允许打开蓝牙
                    finish();
                }
                break;
            case REQUEST_ENABLE_Capture:
                break;
            case REQUEST_IMAGE_CODE:
                if (resultCode != RESULT_OK) {
                    EventBus.getDefault().post(new SelectPicSuccessEvent(SelectPicSuccessEvent.SelectStatus.SELECT_FAILED, null));
                    return;
                }

                Uri originalUri = data.getData();
                String path = null;
                Cursor cursor = getContentResolver().query(originalUri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    path = cursor.getString(1);
                    Log.e("path", "" + path);
                }
                cursor.close();
                EventBus.getDefault().post(new SelectPicSuccessEvent(SelectPicSuccessEvent.SelectStatus.SELECT_SUCCESS, path));
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("________", "--------------------");
        initIntent(intent);
    }


    private void initIntent(Intent intent) {
        String result = intent.getStringExtra("resultString");
        if (result != null) {
            EventBus.getDefault().post(new ConnectSuccessEvent(ConnectSuccessEvent.ConnectState.SCAN_SUCCESS, "", result, null));
        }
    }

    public void startConnect(String macAddress) {
        mBluetoothDevice = mService.getDevByMac(macAddress);
        Log.e("name", "" + mBluetoothDevice.getName());
        mService.connect(mBluetoothDevice);
    }


}
