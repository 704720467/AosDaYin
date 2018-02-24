package com.zp.aosdayin.activity;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.printerhelper.MyBarcode;
import com.google.zxing.BarcodeFormat;
import com.zp.aosdayin.R;
import com.zp.aosdayin.model.LoginConfig;
import com.zp.aosdayin.util.PrintUtil;
import com.zp.aosdayin.util.STPrinter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Jack.WewinPrinterHelper.WwPrint;

/**
 * 老标签打印
 */
public class AosOldLableDaYinActivity extends Activity {
    WwPrint p = new WwPrint();
    private ImageView titleBack;
    private Button submit_btn;
    private Button rest_btn;
    private EditText edt_yundh;
    private CheckBox check_Box;
    private TextView lab_count;
    private static List<String> huobhlist = new ArrayList<String>();
    private String str;
    private Dialog noticeDialog;
    String totalAmount = "";
    // 产品
    String productName = "";
    // 始发站代码
    String senderCode = "";
    // 传单人CODE
    String createUserCode = "";
    // 日期
    String createDate = new SimpleDateFormat("yyMMdd").format(new Date());
    // 时间
    String createTime = new SimpleDateFormat("HHmm").format(new Date());
    private TextView mTvPrintCount;
    String printCount = "0";//打印次数
    private Bitmap fristMap = null;
    private ImageView mFristImageView;
    private View logingLayout;

    private int currentPrintPosition = 0;//当前打印是第几张
    private int iCount = 0;//一共有多少张
    private TextView backText;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                case 1:
                    if (logingLayout != null)
                        logingLayout.setVisibility(msg.what == 0 ? View.VISIBLE : View.GONE);
                    if (edt_yundh != null) {
                        edt_yundh.clearFocus();
                        edt_yundh.setFocusable(false);
                    }
                    break;
                case 2://打印机打印成功
                    backText.setVisibility(View.VISIBLE);
                    if (backText != null) {
                        backText.setText(backText.getText().toString() + "\n" + (String) msg.obj);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aos_oldlabledayin);
        init();
    }

    private void init() {
        backText = (TextView) findViewById(R.id.back_text);
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
        mTvPrintCount = (TextView) findViewById(R.id.print_count);
        check_Box = (CheckBox) findViewById(R.id.checkBox1);
        mFristImageView = (ImageView) findViewById(R.id.imgview);
        edt_yundh.addTextChangedListener(textWatcher);
        logingLayout = findViewById(R.id.logingLayout);
        logingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        submit_btn = (Button) findViewById(R.id.ui_submit_btn);
        submit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                synchronized (this) {
                    tipPrint();
                }
            }
        });
        rest_btn = (Button) findViewById(R.id.ui_rest_btn);
        rest_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                p.close();
                setResult(1);
                finish();
            }
        });

//        findViewById(R.id.ceshitiaoxingma).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (mFristImageView != null)
//                        mFristImageView.setImageBitmap(MyBarcode.createQRCode("1234567891", 25 * 8, 25 * 8, 1, BarcodeFormat.CODE_128));
//                } catch (Exception e) {
//
//                }
//            }
//        });

    }

    private void toPrint() {
        handler.sendEmptyMessage(0);
        if (huobhlist.size() == 0) {
            Toast.makeText(getApplicationContext(), "当前运单无签标可打印！", Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(1);
            return;
        }
        iCount = huobhlist.size();
        try {
            if (iCount == 0) {
                Toast.makeText(getApplicationContext(), "件数为0不能打印！", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(1);
                return;
            }
            // 处理4位总数量
            if (totalAmount.length() == 1)
                totalAmount = "000" + totalAmount;
            if (totalAmount.length() == 2)
                totalAmount = "00" + totalAmount;
            if (totalAmount.length() == 3)
                totalAmount = "0" + totalAmount;
            currentPrintPosition = 0;
            toPrintNext();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(1);
            e.printStackTrace();
            Log.e("", "初次打印错误：" + e.getMessage());
        }
    }

    /**
     * 去打印下一张
     */
    private void toPrintNext() {
        List<String> texts = new ArrayList<String>();
        int i = 0;
        try {
            for (String s : huobhlist) {
                texts.clear();
                texts.add(s);
                texts.add("ACMORS");
                texts.add(totalAmount);
                texts.add(productName);
                texts.add(senderCode);
                texts.add(createUserCode);
                texts.add(createDate);
                texts.add(createTime);
//                Bitmap b = MyBarcode.getBarCode(texts);
//                MyBarcode.saveBitmap("第二个标签" + currentPrintPosition, b);
//                currentPrintPosition++;
//                if (currentPrintPosition > 1)
//                    break;
                PrintUtil.toPrint(AosOldLableDaYinActivity.this, MyBarcode.getBarCode(texts), handler);
            }
            //            SaveServerData();
        } catch (Exception e) {
            Log.e("AosHuoDanDaYinActivity", "打印报错：" + e.getMessage());
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
                        Toast.makeText(getApplicationContext(), "当前扫描或录入的运单号不存在或不正确！", Toast.LENGTH_SHORT).show();
                        edt_yundh.setText("");
                        edt_yundh.requestFocus();
                        return;
                    }
                    for (int i = 0; i < iSize; i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        huobhlist.add(jsonData.get("CargoLabelNo").toString());
                        totalAmount = jsonData.get("TotalAmount").toString();
                        productName = jsonData.get("ProductName").toString();
                        senderCode = jsonData.get("SenderCode").toString();
                        createUserCode = jsonData.get("CreateUserCode").toString();
                        printCount = jsonData.get("PrintTimes").toString();
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
        mTvPrintCount.setText("打印次数：" + printCount + "次");
        if (fristMap != null)
            fristMap.recycle();
        List<String> Texts = new ArrayList<String>();
        Texts.add(huobhlist.get(0));
        Texts.add("ACMORS");
        if (totalAmount.length() == 1)
            totalAmount = "000" + totalAmount;
        if (totalAmount.length() == 2)
            totalAmount = "00" + totalAmount;
        if (totalAmount.length() == 3)
            totalAmount = "0" + totalAmount;
        Texts.add(totalAmount);
        Texts.add(productName);
        Texts.add(senderCode);
        Texts.add(createUserCode);
        Texts.add(createDate);
        Texts.add(createTime);
        fristMap = MyBarcode.getBarCode(Texts);
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
            productName = "";
            senderCode = "";
            createUserCode = "";
            printCount = "0";
            if (mTvPrintCount != null)
                mTvPrintCount.setText("打印次数：" + printCount + "次");
            if (lab_count != null)
                lab_count.setText(String.valueOf(0));
            if (mFristImageView != null)
                mFristImageView.setImageBitmap(null);
        }
    }

    // 保存数据
    public void SaveServerData() {
        int iCount = 0;
        iCount = huobhlist.size();
        if (iCount == 0) {
            return;
        }
        String CargoLabelNo = "";
        for (int i = 0; i < iCount; i++) {
            CargoLabelNo = CargoLabelNo + huobhlist.get(i) + ",";
        }
        if (CargoLabelNo.length() > 0) {
            CargoLabelNo = CargoLabelNo.substring(0, CargoLabelNo.length() - 1);
        }
        // Toast.makeText(getApplicationContext(), CargoLabelNo,
        // Toast.LENGTH_SHORT).show();
        //        String url = "http://aosmis.cnstpl.com/api/v1.ashx";
        //        String url = "http://aosmis.cnstpl.com/api/v0.ashx";
        String url = "http://ob.acmors.com/api/v0.ashx";
        HttpResponse httpResponse = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("apikey", "29bbf68b-b0d7-4b6c-83f1-ec67240c756e"));
            params.add(new BasicNameValuePair("funcid", "submitCargoLabelPrint"));
            params.add(new BasicNameValuePair("cargoLabelNos", CargoLabelNo));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            String result = "";
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            } else {
                result = EntityUtils.toString(httpResponse.getEntity()) + "/////错误代码：" + httpResponse.getStatusLine().getStatusCode();
            }
            Log.e("", "=====>打印数据保存结果：" + result);
            return;
        } catch (Exception xee) {
            Toast.makeText(getApplicationContext(), xee.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public String GetServerData() {
        try {
            HttpPost localHttpPost = new HttpPost("http://aosmis.cnstpl.com/ajax/GetAjaxData.ashx");
            ArrayList<NameValuePair> localArrayList = new ArrayList<NameValuePair>();
            localArrayList.add(new BasicNameValuePair("action", "jsonQuery"));
            localArrayList.add(new BasicNameValuePair("funcid", "TMS_BS_WaybillCargoInfo"));
            //            if (this.check_Box.isChecked())
            //                localArrayList.add(new BasicNameValuePair("where", "waybillId in (select waybillId from TMS_BS_Waybill where waybillNo='" + this.edt_yundh.getText().toString() + "')"));
            //            else
            //                localArrayList.add(new BasicNameValuePair("where", "waybillId in (select waybillId from TMS_BS_Waybill where waybillNo='" + edt_yundh.getText().toString() + "') and isnull(PrintTimes,0)=0"));
            localArrayList.add(new BasicNameValuePair("where", "waybillId in (select waybillId from TMS_BS_Waybill where waybillNo='" + this.edt_yundh.getText().toString() + "')"));

            while (true) {
                localArrayList.add(new BasicNameValuePair("sort", "CargoLabelNo"));
                localArrayList.add(new BasicNameValuePair("order", "asc"));
                localHttpPost.setEntity(new UrlEncodedFormEntity(localArrayList, "UTF-8"));
                HttpResponse localHttpResponse = new DefaultHttpClient().execute(localHttpPost);
                if (localHttpResponse.getStatusLine().getStatusCode() != 200)
                    break;
                return EntityUtils.toString(localHttpResponse.getEntity());

            }
        } catch (Exception localException) {
            Toast.makeText(getApplicationContext(), localException.toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
        return "";
    }

    // post获得客户信息
    public String GeQiansrInfo() {
        LoginConfig loginConfig = new LoginConfig();
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
        String url = "http://" + (new LoginConfig()).getXmppHost() + "/ajax/android_ajax.ashx";
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
        LoginConfig loginConfig = new LoginConfig();
        String url = "http://" + loginConfig.getXmppHost() + "/ajax/android_ajax.ashx";
        // pd.setMessage("正在提交...");
        // pd.show();
        HttpResponse httpResponse = null;
        try {

            String yundh = edt_yundh.getText().toString();
            String userid = loginConfig.getUserid();
            //            String qiansr = edt_qiansr.getText().toString();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("action", "SaveData"));
            params.add(new BasicNameValuePair("savetype", "qianssm"));
            params.add(new BasicNameValuePair("yundh", yundh)); // 运单号
            params.add(new BasicNameValuePair("userid", userid)); // 用户ID
            //            params.add(new BasicNameValuePair("qiansr", qiansr)); // 用户ID
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
                if (logingLayout.getVisibility() == View.GONE)
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
                //                submit_btn.performClick();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void tipPrint() {
        Builder builder = new Builder(AosOldLableDaYinActivity.this);
        String tip = (printCount.equals("0")) ? "是否进行打印？" : "当前打印为第（" + printCount + "）次，可能存在识别风险，请确认是否继续打印？";
        builder.setTitle("提示:");
        builder.setMessage(tip);
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

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        STPrinter.openPrinter();
    }
}
