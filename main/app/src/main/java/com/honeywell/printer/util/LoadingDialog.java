package com.honeywell.printer.util;

/**
 * Created by milton on 16/5/24.
 */


import android.app.ProgressDialog;
import android.content.Context;

import com.honeywell.printer.R;


public class LoadingDialog extends ProgressDialog {
    private String content;

    public LoadingDialog(Context context, String title) {
        super(context, R.style.loadingDialogStyle);
        content = title;
    }

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }


}