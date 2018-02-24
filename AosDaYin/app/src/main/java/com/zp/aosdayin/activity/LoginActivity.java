package com.zp.aosdayin.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.dothantech.printer.IDzPrinter;
import com.zp.aosdayin.R;
import com.zp.aosdayin.model.LoginConfig;
import com.zp.aosdayin.util.STPrinter;


/**
 * 登录.
 *
 * @author shimiso
 */
public class LoginActivity extends Activity {
    private Button btn_print, btn_qiandanprint, btn_xinxiprint,mBtOldPrint, btn_exit;
    protected Context context = null;
    private LoginConfig loginConfig;
    private TextView mTvPringName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        init();
        initSTPrinter();
    }

    private void initSTPrinter() {
        IDzPrinter.PrinterInfo localPrinterInfo = IDzPrinter.PrinterInfo.valueOf((Intent) getIntent().getParcelableExtra("com.dothantech.manager.EXTRA_NDEF_INTENT"));
        if (localPrinterInfo != null) {
            STPrinter.openPrinter(localPrinterInfo.getPrinterAddress());
            mTvPringName.setText(STPrinter.getPrinter().getPrinterName());
            return;
        }
        if (STPrinter.openPrinter())
            mTvPringName.setText(STPrinter.getPrinter().getPrinterName());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化.
     *
     * @author shimiso
     * @update 2012-5-16 上午9:13:01
     */
    protected void init() {
        loginConfig = new LoginConfig();
        btn_print = (Button) findViewById(R.id.ui_btn_print);
        btn_qiandanprint = (Button) findViewById(R.id.Button02);
        btn_xinxiprint = (Button) findViewById(R.id.Button03);
        mBtOldPrint = (Button) findViewById(R.id.Button04);
        btn_exit = (Button) findViewById(R.id.Button01);
        mTvPringName = (TextView) findViewById(R.id.print_name);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setClass(context, AosHuoQianDaYinActivity.class);
                startActivity(intent);

                return;
            }
        });
        btn_qiandanprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setClass(context, AosQianDanDaYinActivity.class);
                startActivity(intent);
                return;
            }
        });
        btn_xinxiprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setClass(context, AosXinXiQianDaYinActivity.class);
                startActivity(intent);
                return;
            }
        });

        mBtOldPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setClass(context, AosOldLableDaYinActivity.class);
                startActivity(intent);
                return;
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExit();
            }
        });
    }


    //屏蔽按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下键盘上返回按钮
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            case KeyEvent.KEYCODE_CALL:
                return true;
            case KeyEvent.KEYCODE_SYM:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_STAR:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    public void onBackPressed() {
        isExit();
    }

    public void isExit() {
        new AlertDialog.Builder(context).setTitle("系统将被关闭，请确认当前打印任务是否结束！").setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).show();
    }


}
