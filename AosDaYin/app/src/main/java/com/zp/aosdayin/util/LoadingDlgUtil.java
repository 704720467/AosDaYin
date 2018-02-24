package com.zp.aosdayin.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;


/**
 * Created by keke on 2017/7/23.
 */

public class LoadingDlgUtil {

    private Context mContext;
    private Dialog mDlg;

    private LoadingDlgUtil(Context mContext) {
        this.mContext = mContext;
        if (mDlg == null) {
            mDlg = new Dialog(mContext);
            mDlg.setCancelable(false);

            ProgressBar progressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyle);

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDlg.setContentView(progressBar, params);
        }
    }

    private static LoadingDlgUtil instance;

    public static LoadingDlgUtil getInstance(Context c) {
        if (instance == null) {
            instance = new LoadingDlgUtil(c);
        }
        return instance;
    }

    public void showLoading() {

        if (mDlg != null && !mDlg.isShowing()) {
            mDlg.show();
        }
    }

    public void cancelLoading() {
        if (mDlg != null && mDlg.isShowing()) {
            mDlg.cancel();
        }
    }

}
