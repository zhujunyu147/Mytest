package com.honeywell.printer.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.TextView;


import com.honeywell.printer.R;
import com.honeywell.printer.model.ViewHolder;

import java.util.List;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/23.
 */
public class ConnectedPrinterListAdapter extends CommonAdapter<BluetoothDevice>{

    public ConnectedPrinterListAdapter(Context context, List<BluetoothDevice> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, BluetoothDevice item) {
        TextView textViewCubeName = helper.getView(R.id.tv_status_name);
        TextView textViewCubeDis = helper.getView(R.id.tv_status_description);
        textViewCubeName.setText(item.getName());
        textViewCubeDis.setText(item.getAddress());
    }
}
