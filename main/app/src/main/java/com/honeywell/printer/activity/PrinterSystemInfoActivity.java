package com.honeywell.printer.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.printer.R;
import com.honeywell.printer.adapter.StatusListAdapter;
import com.honeywell.printer.model.BaseItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujunyu on 16/9/17.
 */
public class PrinterSystemInfoActivity extends BaseCommonActivity {


    private ListView mListView;
    private ImageView mImageViewBack;
    private TextView mTextViewTitle;


    @Override
    void setContentView() {
        setContentView(R.layout.activity_status_detail);
    }

    void initView() {
        mListView = (ListView) findViewById(R.id.lv);
        mImageViewBack = (ImageView) findViewById(R.id.button_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrinterSystemInfoActivity.this.finish();
            }
        });
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewTitle.setText("打印机详细信息");
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
        List<BaseItem> list = (List<BaseItem>) intent.getSerializableExtra("status_list");

        StatusListAdapter statusListAdapter = new StatusListAdapter(this, filterData(list), R.layout.activity_status_item);
        mListView.setAdapter(statusListAdapter);

    }


    private List<BaseItem> filterData(List<BaseItem> list) {
        List<BaseItem> newList = new ArrayList<>();
        for (BaseItem baseItem : list) {
            if ("FirmwareVersion".equals(baseItem.key)
                    || "WifiMACAddress".equals(baseItem.key)
                    || "SerialNumber".equals(baseItem.key)
                    || "PartNumber".equals(baseItem.key)
                    || "PrinterMileage".equals(baseItem.key)
                    || "PrinterStatus".equals(baseItem.key)) {
                newList.add(baseItem);
            }

        }
        return newList;
    }


}
