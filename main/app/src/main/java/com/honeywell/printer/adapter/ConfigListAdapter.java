package com.honeywell.printer.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.honeywell.printer.R;
import com.honeywell.printer.model.ViewHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Junyu.zhu@Honeywell.com on 16/8/23.
 */
public class ConfigListAdapter extends CommonAdapter<String> {
    private HashMap<String, Boolean> states = new HashMap<String, Boolean>();//用于记录每个RadioButton的状态，并保证只可选一个
    public  String selectName;
    public ConfigListAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(final ViewHolder helper, final String item) {
        TextView textView = helper.getView(R.id.tv_filename);
        textView.setText(item);
        final RadioButton radio = helper.getView(R.id.radio_btn);
        final int position = helper.getPosition();
        radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //重置，确保最多只有一项被选中
                for (String key : states.keySet()) {
                    states.put(key, false);
                }
                states.put(String.valueOf(position), radio.isChecked());
                selectName = item;
                ConfigListAdapter.this.notifyDataSetChanged();
            }
        });
        boolean res = false;
        if (states.get(String.valueOf(position)) == null || states.get(String.valueOf(position)) == false) {
            res = false;
            states.put(String.valueOf(position), false);
        } else {
            res = true;
        }
        radio.setChecked(res);
    }
}
