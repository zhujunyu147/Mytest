package com.honeywell.printer.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.honeywell.net.core.TubeConfig;
import com.honeywell.net.core.TubeOptions;
import com.honeywell.net.exception.TubeException;
import com.honeywell.net.listener.JSONTubeListener;
import com.honeywell.net.listener.StringTubeListener;
import com.honeywell.net.mian.FastTube;
import com.honeywell.net.utils.Logger;
import com.honeywell.printer.R;
import com.honeywell.printer.service.BluetoothManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 搜索好友页面
 *
 * @author zhujunyu
 */
public class SearchPrinterFragment extends MyFragment {

    private View view;
    private boolean isRefresh = true;// 获取数据成功还是失败 为后面执行刷新还是加载 成功或者失败

    private final String testUrl1 = "http://open.play.cn/api/v2/mobile/channel/content.json?channel_id=701&terminal_id=245&current_page=0&rows_of_page=20&order_id=0";
    private FastTube mFastTube;
    private static final TubeOptions NORMAL_OPTIONS = new TubeOptions.Builder()
            .setHostKey("TEST").setReconnectionTimes(10).create();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_search, container, false);

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
        }
    }

    /**
     * 适配器填充listView数据
     */
    private void initView() {
        Button mButton = (Button) view.findViewById(R.id.btn_search_device);
        mFastTube = FastTube.getInstance();
        mFastTube.init(new TubeConfig.Builder().setThreadCount(10).create());


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "此功能暂未开放", Toast.LENGTH_SHORT).show();
                //搜索蓝牙打印机设备
//                scanDevice();
//                new Thread() {
//                    @Override
//                    public void run() {
//                        mFastTube.getString(testUrl1, NORMAL_OPTIONS, new StringTubeListener<JSONObject>() {
//                            @Override
//                            public JSONObject doInBackground(String water) throws Exception {
//                                Log.e("~~~~~~~", "" + water);
//                                return null;
//                            }
//
//                            @Override
//                            public void onSuccess(JSONObject result) {
//                                if (result != null) {
//                                    Logger.d("wei.han", result.toString());
//                                }
//                            }
//
//                            @Override
//                            public void onFailed(TubeException e) {
//
//                            }
//                        });
//
//
//
//
////                        mFastTube.getJSON(testUrl1, NORMAL_OPTIONS, new JSONTubeListener<JSONObject>() {
////
////                            @Override
////                            public JSONObject doInBackground(JSONObject water) throws Exception {
////                                return null;
////                            }
////
////                            @Override
////                            public void onSuccess(JSONObject jsonObject) {
////
////                            }
////
////                            @Override
////                            public void onFailed(TubeException e) {
////
////                            }
////                        });
//
//                    }
//                }.start();

            }
        });


    }

    private void scanDevice() {
        BluetoothManager.getInstance().startBLEScan(new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                final BleAdvertisedData badata = BleUtil.parseAdertisedData(scanRecord);
                Log.e("~~~~", "" + device.getAddress());
                Log.e("！！！！", "" + device.getName());
                String deviceName = device.getName();
                if (deviceName == null) {
                    deviceName = badata.getName();
                    Log.e("@@@", "" + deviceName);
                }

            }
        });
    }

    static final public class BleUtil {
        private final static String TAG = BleUtil.class.getSimpleName();

        public static BleAdvertisedData parseAdertisedData(byte[] advertisedData) {
            List<UUID> uuids = new ArrayList<UUID>();
            String name = null;
            if (advertisedData == null) {
                return new BleAdvertisedData(uuids, name);
            }

            ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
            while (buffer.remaining() > 2) {
                byte length = buffer.get();
                if (length == 0) break;

                byte type = buffer.get();
                switch (type) {
                    case 0x02: // Partial list of 16-bit UUIDs
                    case 0x03: // Complete list of 16-bit UUIDs
                        while (length >= 2) {
                            uuids.add(UUID.fromString(String.format(
                                    "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                            length -= 2;
                        }
                        break;
                    case 0x06: // Partial list of 128-bit UUIDs
                    case 0x07: // Complete list of 128-bit UUIDs
                        while (length >= 16) {
                            long lsb = buffer.getLong();
                            long msb = buffer.getLong();
                            uuids.add(new UUID(msb, lsb));
                            length -= 16;
                        }
                        break;
                    case 0x09:
                        byte[] nameBytes = new byte[length - 1];
                        buffer.get(nameBytes);
                        try {
                            name = new String(nameBytes, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        buffer.position(buffer.position() + length - 1);
                        break;
                }
            }
            return new BleAdvertisedData(uuids, name);
        }
    }


    public static class BleAdvertisedData {
        private List<UUID> mUuids;
        private String mName;

        public BleAdvertisedData(List<UUID> uuids, String name) {
            mUuids = uuids;
            mName = name;
        }

        public List<UUID> getUuids() {
            return mUuids;
        }

        public String getName() {
            return mName;
        }
    }
}
