package com.honeywell.printer.adapter;

import android.content.Context;
import android.widget.TextView;


import com.honeywell.printer.R;
import com.honeywell.printer.model.BaseItem;
import com.honeywell.printer.model.ViewHolder;

import java.util.List;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/23.
 */
public class StatusListAdapter extends CommonAdapter<BaseItem>{

    public StatusListAdapter(Context context, List<BaseItem> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolder helper, BaseItem item) {
        TextView textViewCubeName = helper.getView(R.id.tv_status_name);
        TextView textViewCubeDis = helper.getView(R.id.tv_status_description);
        textViewCubeName.setText(item.key);
        textViewCubeDis.setText(item.value);
    }
}
