package com.honeywell.printer.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.printer.R;

/**
 * Created by zhujunyu on 16/9/27.
 */
public class AboutHoneywellActivity extends BaseCommonActivity {
    private TextView mTextView;
    private ImageView mImageViewBack;
    private TextView mTextViewTitle;
    @Override
    void setContentView() {
        setContentView(R.layout.activity_about_honeywell);
    }

    @Override
    void initView() {
        mTextView = (TextView) findViewById(R.id.tv);
        mImageViewBack = (ImageView) findViewById(R.id.button_back);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutHoneywellActivity.this.finish();
            }
        });
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewTitle.setText("关于 Honeywell");
    }

    @Override
    void initEvent() {

    }

    @Override
    void initData() {
        mTextView.setText("霍尼韦尔国际（Honeywell International）是一家营业额达300多亿美元的多元化高科技和制造企业，在全球，其业务涉及：航空产品和服务，楼宇、家庭和工业控制技术，汽车产品，涡轮增压器，以及特殊材料。霍尼韦尔公司总部位于美国新泽西州莫里斯镇。");
    }
}
