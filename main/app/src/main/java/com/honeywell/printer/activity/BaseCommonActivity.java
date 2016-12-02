package com.honeywell.printer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.honeywell.printer.util.LoadingDialog;

/**
 * Created by zhujunyu on 16/9/27.
 */
public abstract class BaseCommonActivity extends Activity{

    private LoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initView();
        initEvent();
        initData();
    }

    abstract void setContentView();

    abstract void initView();

    abstract void initEvent();

    abstract void initData();

    public void showLoadingDialog() {
        if (null == mLoadingDialog) {
            initLoadingDialog();
        }
        mLoadingDialog.show();
    }

    private void initLoadingDialog() {
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != mLoadingDialog && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
