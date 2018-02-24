package com.zp.aosdayin.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printerhelper.MyBarcode;
import com.zp.aosdayin.R;
import com.zp.aosdayin.model.LoginConfig;
import com.zp.aosdayin.util.PrintUtil;
import com.zp.aosdayin.util.ValidateUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Jack.WewinPrinterHelper.WwPrint;


public class AosQianDanDaYinActivity extends Activity {
    public boolean flag = true;
    WwPrint p = new WwPrint();
    ImageView imageView1;
    private ImageView titleBack;
    private Button submit_btn;
    private Button rest_btn;
    private EditText edt_huobh, edt_yundh;
    private TextView lab_count;
    private ArrayAdapter countiesAdapter;
    private List<String> allCounties = new ArrayList<String>();
    private List<String> serverhuobhlist = new ArrayList<String>();
    private List<String> huobhlist = new ArrayList<String>();
    private String str;
    private int iCount = 0;
    private LoginConfig loginConfig;
    private Dialog noticeDialog;
    private TextView lab_tifk, lab_daishk, lab_yaoqhd, lab_huidlx, lab_huidfs, lab_huidsm;
    private EditText edt_qiansr;
    String totalAmount = "";
    // 产品
    String productName = "0";
    // 始发站代码
    String senderCode = "";
    // 传单人CODE
    String createUserCode = "";
    // 日期
    String createDate = new java.text.SimpleDateFormat("yyMMdd").format(new java.util.Date());
    // 时间
    String createTime = new java.text.SimpleDateFormat("HHmm").format(new java.util.Date());
    private ImageView mFristImageView;
    private Bitmap fristMap = null;
    private View logingLayout;
    private boolean printState = false;//记录打印的状态，false打印失败，true ：打印成功

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (logingLayout != null)
                logingLayout.setVisibility(msg.what == 0 ? View.VISIBLE : View.GONE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginConfig = new LoginConfig();
        setContentView(R.layout.aos_qiandandayin);
        init();
    }

    private void init() {
        titleBack = (ImageView) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                p.close();
                setResult(1);
                finish();
            }
        });

        edt_yundh = (EditText) findViewById(R.id.ui_yundh_input);
        lab_count = (TextView) findViewById(R.id.ui_count_input);
        edt_yundh.addTextChangedListener(textWatcher);
        submit_btn = (Button) findViewById(R.id.ui_submit_btn);
        logingLayout = findViewById(R.id.logingLayout);
        logingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mFristImageView = (ImageView) findViewById(R.id.imgview);
        submit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 询问是否确认领用
                AlertDialog.Builder builder = new Builder(AosQianDanDaYinActivity.this);
                builder.setTitle("提示");
                builder.setMessage("是否进行打印？");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        synchronized (this) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    toPrint();
                                }
                            }).start();
                            dialog.dismiss();
                        }
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                noticeDialog = builder.create();
                noticeDialog.show();

            }
        });
        rest_btn = (Button) findViewById(R.id.ui_rest_btn);
        rest_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                p.close();
                // edt_huobh.setText("");
                setResult(1);
                finish();
            }
        });

    }

    private void toPrint() {
        if (printState) {
            Toast.makeText(this, "请退出再次进入", Toast.LENGTH_LONG).show();
            return;
        }
        handler.sendEmptyMessage(0);
        int iCount = huobhlist.size();
        // 打印事件
        if (huobhlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "当前运单无签标可打印！", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(1);
            return;
        }
        try {
            // 处理4位总数量
            if (totalAmount.length() == 1) {
                totalAmount = "000" + totalAmount;
            }
            if (totalAmount.length() == 2) {
                totalAmount = "00" + totalAmount;
            }
            if (totalAmount.length() == 3) {
                totalAmount = "0" + totalAmount;
            }
            // final List<Bitmap> list = new
            // ArrayList<Bitmap>();
            printState = false;
            List<String> Texts = new ArrayList<String>();
            for (int j = 0; j < iCount; j++) {
                Texts.clear();
                Texts.add(huobhlist.get(j));
                Texts.add("ACMORS");
                Texts.add(totalAmount);
                Texts.add(productName);
                Texts.add(senderCode);
                Texts.add(createUserCode);
                Texts.add(createDate);
                Texts.add(createTime);
                //                                    Bitmap b = MyBarcode.QS_02Q(Texts);
                //                                    MyBarcode.saveBitmap("第二个标签" + j, b);
                //                                    PrintUtil.toPrint(AosQianDanDaYinActivity.this, b);
                PrintUtil.toPrint(AosQianDanDaYinActivity.this, MyBarcode.QS_02Q(Texts), handler);
            }
            printState = true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            handler.sendEmptyMessage(1);
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            Log.d("TAG", "afterTextChanged--------------->");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            Log.d("TAG", "beforeTextChanged--------------->");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("TAG", "onTextChanged--------------->");
            str = edt_yundh.getText().toString();
            clearOldData();
            // 总件数
            if (str.trim().length() == 12) {
                String returnData = GetServerData();
                // 将json字符串转换为json对象
                try {
                    JSONObject jsonObj = new JSONObject(returnData);
                    JSONArray jsonArray = new JSONArray(jsonObj.getString("data"));
                    int iSize = jsonArray.length();
                    lab_count.setText(String.valueOf(iSize));
                    if (iSize == 0) {
                        Toast.makeText(getApplicationContext(), "当前运单无签标可打印！", Toast.LENGTH_SHORT).show();
                        edt_yundh.setText("");
                        edt_yundh.requestFocus();
                        return;
                    }
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        huobhlist.add(jsonData.get("SignBillNo").toString());
                        totalAmount = jsonData.get("SignAmount").toString();
                        senderCode = jsonData.get("SenderCode").toString();
                        createUserCode = jsonData.get("CreateUserCode").toString();
                    }
                    toShowFristImage();
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    };

    private void toShowFristImage() {
        List<String> Texts = new ArrayList<String>();
        Texts.clear();
        Texts.add(huobhlist.get(0));
        Texts.add("ACMORS");
        if (totalAmount.length() == 1) {
            totalAmount = "000" + totalAmount;
        }
        if (totalAmount.length() == 2) {
            totalAmount = "00" + totalAmount;
        }
        if (totalAmount.length() == 3) {
            totalAmount = "0" + totalAmount;
        }
        if (fristMap != null)
            fristMap.recycle();
        Texts.add(totalAmount);
        Texts.add(productName);
        Texts.add(senderCode);
        Texts.add(createUserCode);
        Texts.add(createDate);
        Texts.add(createTime);
        fristMap = MyBarcode.QS_02Q(Texts);
        if (mFristImageView != null)
            mFristImageView.setImageBitmap(fristMap);
    }

    /**
     * 清空已有数据
     */
    private void clearOldData() {
        if (huobhlist == null)
            huobhlist = new ArrayList<String>();
        if (huobhlist.size() > 0) {
            huobhlist.clear();
            totalAmount = "";
            senderCode = "";
            createUserCode = "";
            if (lab_count != null)
                lab_count.setText(String.valueOf(0));
            if (mFristImageView != null)
                mFristImageView.setImageBitmap(null);
        }
    }

    // post获得客户信息
    public String GetServerData() {
        String url = "http://aosmis.cnstpl.com/ajax/GetAjaxData.ashx";
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "jsonQuery"));
            params.add(new BasicNameValuePair("funcid", "TMS_BS_WaybillSignBill"));
            params.add(new BasicNameValuePair("where", "waybillId in (select waybillId from TMS_BS_Waybill where waybillNo='" + edt_yundh.getText().toString() + "')"));
            params.add(new BasicNameValuePair("sort", "SignBillNo"));
            params.add(new BasicNameValuePair("order", "asc"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        } catch (Exception xee) {
            Toast.makeText(getApplicationContext(), xee.toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    // post获得客户信息
    public String GeQiansrInfo() {
        String url = "http://" + loginConfig.getXmppHost() + "/ajax/android_ajax.ashx";
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "GetData"));
            params.add(new BasicNameValuePair("gettype", "qiansr"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        } catch (Exception xee) {
            return "";
        }
    }

    // post获得客户信息
    public String GethuobhbyyundhInfo(String huobh) {
        String url = "http://" + loginConfig.getXmppHost() + "/ajax/android_ajax.ashx";
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "GetData"));
            params.add(new BasicNameValuePair("gettype", "yundhdata"));
            params.add(new BasicNameValuePair("huobh", huobh));
            params.add(new BasicNameValuePair("danjzt", "取货完成"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        } catch (Exception xee) {
            return "";
        }
    }

    private boolean checkData() {
        boolean checked = false;
        checked = (!ValidateUtil.isEmpty(edt_yundh, "运单号"));
        return checked;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        getContentResolver();
        /**
         * 因为两种方式都用到了startActivityForResult方法， 这个方法执行完后都会执行onActivityResult方法，
         * 所以为了区别到底选择了那个方式获取图片要进行判断，
         * 这里的requestCode跟startActivityForResult里面第二个参数对应
         */
        if (requestCode == 0) {
            try {
                Toast.makeText(getApplicationContext(), "图片0！", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } else if (requestCode == 1) {

            try {
                if (resultCode == -1) {
                    Toast.makeText(getApplicationContext(), "拍照成功！", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    // postSAVE数据
    public String SaveData() {
        String url = "http://" + loginConfig.getXmppHost() + "/ajax/android_ajax.ashx";
        // pd.setMessage("正在提交...");
        // pd.show();
        HttpResponse httpResponse = null;
        try {

            String yundh = edt_yundh.getText().toString();
            String userid = loginConfig.getUserid();
            String qiansr = edt_qiansr.getText().toString();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "SaveData"));
            params.add(new BasicNameValuePair("savetype", "qianssm"));
            params.add(new BasicNameValuePair("yundh", yundh)); // 运单号
            params.add(new BasicNameValuePair("userid", userid)); // 用户ID
            params.add(new BasicNameValuePair("qiansr", qiansr)); // 用户ID
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // pd.dismiss();
                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        } catch (Exception xee) {
            Log.d("count", xee.toString());
            return xee.toString();
        }

    }


    // post获得信息
    public String GetqiansxqDataInfo() {
        String url = "http://" + loginConfig.getXmppHost() + "/ajax/android_ajax.ashx";
        String userid = loginConfig.getUserid();
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "GetData"));
            params.add(new BasicNameValuePair("gettype", "qiansxq"));
            params.add(new BasicNameValuePair("weitdh", edt_yundh.getText().toString()));
            params.add(new BasicNameValuePair("userid", userid));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            } else {
                return "";
            }
        } catch (Exception xee) {
            return "";
        }
    }

    // 屏蔽按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下键盘上返回按钮
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                setResult(1);
                finish();
                return true;
            case KeyEvent.KEYCODE_BACK:
                rest_btn.performClick();
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
            case KeyEvent.KEYCODE_MENU:
                submit_btn.performClick();
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
    protected void onResume() {
        super.onResume();
    }
}
