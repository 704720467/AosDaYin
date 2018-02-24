package com.example.printerhelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBarcode {
    public static String VERTICAL = "vertical";
    public static String HORIZONTAL = "horizontal";

    private static int marginW = 0;

    private static BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

    public static Bitmap creatBarcode(Context context, String contents, String midString, int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap ruseltBitmap = null;
        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
            Bitmap codeBitmap = creatCodeBitmap(contents, midString, desiredWidth + 2 * marginW, desiredHeight, context);
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(0, desiredHeight));
        } else {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat, desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    /**
     * 锟斤拷杀锟斤拷锟斤拷Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap creatCodeBitmap(String contents, String midString, int width, int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents + "\n" + midString);
        tv.setHeight(height + 40);
        tv.setIncludeFontPadding(false);
        tv.setTextSize(12f);
        //		tv.setFitsSystemWindows(true);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷Bitmap
     *
     * @param contents      锟斤拷要锟斤拷傻锟斤拷锟斤拷�?
     * @param format        锟斤拷锟斤拷锟斤�?
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @throws WriterException
     */
    protected static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth, desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷印锟斤拷锟街碉拷BitMap
     *
     * @param contents
     * @param width
     * @param height
     * @param textSize
     * @param angle
     * @return
     * @date 2013-11-7
     */
    public static Bitmap creatContentBitmap(String contents, int width, int height, float textSize, float angle) {
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);// 锟斤拷锟斤拷�?��斤拷锟铰碉拷位图
        Canvas cv = new Canvas(newb);

        // 锟斤拷锟斤拷锟斤拷锟斤拷
        Paint paint = new Paint();
        paint.setTypeface(Typeface.SERIF);
        paint.setFakeBoldText(false);
        paint.setTextAlign(Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setFilterBitmap(true);// 去锟斤拷锟斤�?

        float startY = 20.0f;
        List<String> list = StringUtil.autoSplit(contents, paint, width - 20);// 锟饺分革拷锟街凤�?
        for (int i = 0; i < list.size(); i++) {
            drawText(cv, list.get(i), 10f, startY, paint, angle);// 锟斤拷锟斤拷�?
            startY = startY + textSize + 1;
        }
        cv.drawBitmap(newb, 0, 0, null);// 锟斤�?0,0锟斤拷昕硷拷锟斤拷锟皆达拷?
        return newb;
    }

    public static Bitmap CreatesmallImage(String ipnumber, String address, String gfx, String username) {
        int width = 45 * 8;
        int height = 30 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas cv = new Canvas(newb);
        cv.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        // Bitmap codemap = encodeAsBitmap(code, BarcodeFormat.CODE_128, 360,
        // 38);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setFakeBoldText(false);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Align.LEFT);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        int textSize = 24;
        paint.setTextSize(textSize);

        // A
        drawText(cv, ipnumber, 10, 30, paint, 0);
        int startY = 60;
        List<String> list = StringUtil.autoSplit(address, paint, 340);
        if (list.size() == 1) {
            startY += 10;
        }
        for (int i = 0; i < list.size(); i++) {
            if (i > 3)
                break;
            drawText(cv, list.get(i), 10, startY, paint, 0);
            startY = startY + textSize + 3;
        }

        // B
        textSize = 24;
        drawText(cv, username, 350, 210, paint, 180);
        startY = 160;
        int startYY = 180;
        list = StringUtil.autoSplit(gfx, paint, 340);
        for (int i = 0; i < list.size(); i++) {
            if (i > 3)
                break;
            drawText(cv, list.get(i), 350, startYY, paint, 180);
            startYY = startYY - textSize - 2;
        }
        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    /**
     * @param str
     * @param qr_width
     * @param qr_height
     * @return
     * @throws WriterException
     * @date 2013-7-10
     */
    public static Bitmap createQRCode(String str, int qr_width, int qr_height, int margin, BarcodeFormat barcodeFormat) throws WriterException {
        int BLACK = 0xff000000;

        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MAX_SIZE, new Dimension(qr_width, qr_height));
        hints.put(EncodeHintType.MIN_SIZE, new Dimension(qr_width, qr_height));

        BitMatrix matrix = new MultiFormatWriter().encode(str, barcodeFormat, qr_width, qr_height, hints);
//        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, qr_width, qr_height, hints);
//        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_39, qr_width, qr_height, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 锟斤拷要锟斤拷锟斤拷锟街凤拷
     *
     * @param msg
     * @return
     * @date 2013-11-12
     */
    public static String handleString(String msg) {
        if (null != msg && msg.split("\\)\\(").length == 4) {
            String str[] = msg.split("\\)\\(");
            String newMsg = str[0] + ")~(" + str[1] + ")(" + str[2] + ")~(" + str[3];
            return newMsg;
        } else {
            return msg;
        }
    }

    public static Bitmap CT40_240(List<String> Texts) {
        int width = 240 * 8;
        int height = 40 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(90);

        if (Texts.size() < 1)
            return newb;
        drawText(cv, Texts.get(0), 120, 33 * 8, paint, 0);

        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    public static Bitmap CT40_100(List<String> Texts) {
        int width = 100 * 8;
        int height = 45 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(30);

        if (Texts.size() < 5)
            return newb;

        int x = 2 * 8, y = 17 * 8;
        // 设备名称
        List<String> sbmcList = StringUtil.autoSplit(Texts.get(0), paint, width - 10 * 8);
        for (String str : sbmcList) {
            drawText(cv, str, x, y, paint, 0);
            y += 40;
        }

        // 设备编码
        List<String> sbbmList = StringUtil.autoSplit(Texts.get(1), paint, width - 24 * 8);
        for (String str : sbbmList) {
            drawText(cv, str, x, y, paint, 0);
            y += 40;
        }

        // 启用日期
        List<String> qyrqList = StringUtil.autoSplit(Texts.get(2), paint, width - 24 * 8);
        for (String str : qyrqList) {
            drawText(cv, str, x, y, paint, 0);
            y += 40;
        }

        // 责任人及电话
        List<String> zrrList = StringUtil.autoSplit(Texts.get(3), paint, width - 24 * 8);
        for (String str : zrrList) {
            drawText(cv, str, x, y, paint, 0);
            y += 40;
        }

        // 二维码
        String qrcode = Texts.get(4);
        if (qrcode.length() > 0) {
            Bitmap codemap = null;
            try {
                codemap = createQRCode(qrcode, 23 * 8, 23 * 8, 1, BarcodeFormat.QR_CODE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (codemap != null)
                cv.drawBitmap(codemap, 620, 22 * 8, null);
        }

        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    public static Bitmap QS_05F(List<String> Texts) {
        int width = 64 * 8;
        int height = 32 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(28);

        if (Texts.size() < 5)
            return newb;

        int x = 16 * 8, y = 38;
        // 光路编码
        List<String> sbmcList = StringUtil.autoSplit(Texts.get(0), paint, width - 15 * 8);
        for (String str : sbmcList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        // 业务名称
        List<String> sbbmList = StringUtil.autoSplit(Texts.get(1), paint, width - 15 * 8);
        for (String str : sbbmList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        int xx = 63 * 8, yy = 29 * 8;
        // From
        List<String> qyrqList = StringUtil.autoSplit(Texts.get(2), paint, width - 2 * 8);
        for (String str : qyrqList) {
            drawText(cv, str, xx, yy, paint, 180);
            yy -= 30;
        }

        // To
        List<String> zrrList = StringUtil.autoSplit(Texts.get(3), paint, width - 2 * 8);
        for (String str : zrrList) {
            drawText(cv, str, xx, yy, paint, 180);
            yy -= 30;
        }

        // 二维码
        String qrcode = Texts.get(4);
        if (qrcode.length() > 0) {
            Bitmap codemap = null;
            try {
                codemap = createQRCode(qrcode, 15 * 8, 15 * 8, 1, BarcodeFormat.QR_CODE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (codemap != null)
                cv.drawBitmap(codemap, 10, 10, null);
        }

        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    /**
     * 生成图片
     *
     * @param Texts
     * @return
     */
    public static Bitmap QS_02F(List<String> Texts) {
        return QS_02F(Texts, BarcodeFormat.QR_CODE);
    }

    /**
     * 生成一维条形码
     *
     * @param Texts
     * @return
     */
    public static Bitmap getBarCode(List<String> Texts) {
        return QS_02F(Texts, BarcodeFormat.CODE_128);
    }

    /**
     * 生成 条形码有很多种类，二维(条形)码就是其中一种
     *
     * @param Texts         要生成的数据
     * @param barcodeFormat 条形码的格式
     * @return
     */
    public static Bitmap QS_02F(List<String> Texts, BarcodeFormat barcodeFormat) {
        int width = 49 * 8;
        int height = 30 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);
        cv.drawRGB(255, 255, 255);
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(36);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);

        if (Texts.size() < 2)
            return newb;

        int x = 4 * 8, y = 29 * 8;
        // 标识
        List<String> idList = StringUtil.autoSplit(Texts.get(0), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // 标识
        x = 29 * 8;
        y = 6 * 8;
        idList = StringUtil.autoSplit(Texts.get(1), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 总件数
        x = 26 * 8;
        y = 11 * 8;
        idList = StringUtil.autoSplit(Texts.get(2), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 产品名称
        x = 39 * 8;
        y = 11 * 8;
        idList = StringUtil.autoSplit(Texts.get(3), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 始发代码
        x = 26 * 8;
        y = 16 * 8;
        idList = StringUtil.autoSplit(Texts.get(4), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // 时间
        x = 37 * 8;
        y = 16 * 8;
        idList = StringUtil.autoSplit(Texts.get(7), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        x = 26 * 8;
        y = 21 * 8;
        // 传单人
        idList = StringUtil.autoSplit(Texts.get(5), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        x = 26 * 8;
        y = 25 * 8;
        // 打印时间
        //paint.setTextSize(30);
        idList = StringUtil.autoSplit(Texts.get(6), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // 二维码
        String qrcode = Texts.get(0);
        if (qrcode.length() > 0) {
            Bitmap codemap = null;
            try {
                codemap = createQRCode(qrcode, 25 * 8, 25 * 8, 1, barcodeFormat);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (codemap != null)
                cv.drawBitmap(codemap, 1, 1, null);
        }
        return newb;
    }

    public static void saveBitmap(String picName, Bitmap bm) {

        File f = new File("/sdcard/360/", picName + ".png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("===========", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static Bitmap QS_02Q(List<String> Texts) {
        int width = 49 * 8;
        int height = 30 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);
        cv.drawRGB(255, 255, 255);
        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(36);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);

        if (Texts.size() < 2)
            return newb;

        int x = 4 * 8, y = 29 * 8;
        // 标识
        List<String> idList = StringUtil.autoSplit(Texts.get(0), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // 标识
        x = 29 * 8;
        y = 6 * 8;
        idList = StringUtil.autoSplit(Texts.get(1), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 总件数
        x = 26 * 8;
        y = 11 * 8;
        idList = StringUtil.autoSplit(Texts.get(2), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 产品名称
        //		x = 39 * 8;
        //		y = 11 * 8;
        //		idList = StringUtil.autoSplit(Texts.get(3), paint,
        //				width - 2 * 8);
        //		for (String str : idList) {
        //			drawText(cv, str, x, y, paint, 0);
        //			y += 26;
        //		}

        // 始发代码
        x = 26 * 8;
        y = 16 * 8;
        idList = StringUtil.autoSplit(Texts.get(4), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // //时间
        //		x = 37 * 8;
        //		y = 16 * 8;
        //		idList = StringUtil.autoSplit(Texts.get(7), paint,
        //				width - 2 * 8);
        //		for (String str : idList) {
        //			drawText(cv, str, x, y, paint, 0);
        //			y += 26;
        //		}
        x = 26 * 8;
        y = 21 * 8;
        // 传单人
        idList = StringUtil.autoSplit(Texts.get(5), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        x = 26 * 8;
        y = 25 * 8;
        // 打印时间
        //paint.setTextSize(30);
        idList = StringUtil.autoSplit(Texts.get(6), paint, width - 2 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }
        // 二维码
        String qrcode = Texts.get(0);
        if (qrcode.length() > 0) {
            Bitmap codemap = null;
            try {
                codemap = createQRCode(qrcode, 25 * 8, 25 * 8, 1, BarcodeFormat.QR_CODE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (codemap != null)
                cv.drawBitmap(codemap, 1 * 8, 1 * 8, null);
        }

        Bitmap lastmap = newb;
        return lastmap;
    }

    public static Bitmap QS_02T(List<String> Texts) {
        int width = 38 * 8;
        int height = 25 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(24);

        if (Texts.size() < 2)
            return newb;

        int x = 1 * 8, y = 7 * 8;
        // ID
        List<String> idList = StringUtil.autoSplit(Texts.get(0), paint, width - 3 * 8);
        for (String str : idList) {
            drawText(cv, str, x, y, paint, 0);
            y += 26;
        }

        // 二维码
        String qrcode = Texts.get(1);
        if (qrcode.length() > 0) {
            Bitmap codemap = null;
            try {
                codemap = createQRCode(qrcode, 12 * 8, 12 * 8, 1, BarcodeFormat.QR_CODE);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (codemap != null)
                cv.drawBitmap(codemap, 12 * 8, 14 * 8, null);
        }

        Bitmap lastmap = rotate(newb, 0);
        return lastmap;
    }

    public static Bitmap G50_70(List<String> Texts) {
        int width = 70 * 8;
        int height = 50 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(30);

        if (Texts.size() < 4)
            return newb;

        int x = 2 * 8, y = 18 * 8;
        // 光缆段名称
        List<String> gldmcList = StringUtil.autoSplit(Texts.get(0), paint, width - 3 * 8);
        for (String str : gldmcList) {
            drawText(cv, str, x, y, paint, 0);
            y += 36;
        }

        // 光缆型号
        List<String> glxhList = StringUtil.autoSplit(Texts.get(1), paint, width - 3 * 8);
        for (String str : glxhList) {
            drawText(cv, str, x, y, paint, 0);
            y += 36;
        }

        // 建设时间
        List<String> jssjList = StringUtil.autoSplit(Texts.get(2), paint, width - 3 * 8);
        for (String str : jssjList) {
            drawText(cv, str, x, y, paint, 0);
            y += 36;
        }

        // 工程名称
        List<String> gcmcList = StringUtil.autoSplit(Texts.get(3), paint, width - 3 * 8);
        for (String str : gcmcList) {
            drawText(cv, str, x, y, paint, 0);
            y += 36;
        }

        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    public static Bitmap CT35_70(List<String> Texts) {
        int width = 70 * 8;
        int height = 35 * 8;
        Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        Canvas cv = new Canvas(newb);

        Paint paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(25);

        if (Texts.size() < 4)
            return newb;

        int x = 2 * 8, y = 15 * 8;
        // 设备名称
        List<String> sbmcList = StringUtil.autoSplit(Texts.get(0), paint, width - 3 * 8);
        for (String str : sbmcList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        // 设备编码
        List<String> sbbmList = StringUtil.autoSplit(Texts.get(1), paint, width - 3 * 8);
        for (String str : sbbmList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        // 上联PON口
        List<String> slponList = StringUtil.autoSplit(Texts.get(2), paint, width - 3 * 8);
        for (String str : slponList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        // 分光等级
        List<String> fgdjList = StringUtil.autoSplit(Texts.get(3), paint, width - 3 * 8);
        for (String str : fgdjList) {
            drawText(cv, str, x, y, paint, 0);
            y += 30;
        }

        Bitmap lastmap = rotate(newb, 90);
        return lastmap;
    }

    public static void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle) {
        if (angle != 0) {
            canvas.rotate(angle, x, y);
        }
        canvas.drawText(text, x, y, paint);
        if (angle != 0) {
            canvas.rotate(-angle, x, y);
        }
    }

    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(second.getWidth() + marginW, first.getHeight() + second.getHeight(), Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }

    protected static Bitmap mixtureBitmap2(Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth() + second.getWidth() + marginW, second.getHeight(), Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }

    /**
     * 锟斤拷锟斤拷锟斤拷喜锟斤拷锟斤拷锟酵硷拷?
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground, String fangxiang) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();

        Bitmap newmap = null;

        if (fangxiang.equals(MyBarcode.HORIZONTAL)) {
            newmap = Bitmap.createBitmap(bgWidth + fgWidth, fgHeight, Config.ARGB_8888);
        } else if (fangxiang.equals(MyBarcode.VERTICAL)) {
            newmap = Bitmap.createBitmap(fgWidth, bgHeight + fgHeight, Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(newmap);
        if (fangxiang.equals(MyBarcode.HORIZONTAL)) {
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawBitmap(foreground, bgWidth, 0, null);
        } else if (fangxiang.equals(MyBarcode.VERTICAL)) {
            canvas.drawBitmap(foreground, 0, 0, null);
            canvas.drawBitmap(background, 5, fgHeight - 10, null);
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }

    /**
     * 锟斤拷转位图
     *
     * @param b
     * @param degrees
     * @return
     * @date 2013-11-7
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle(); // Bitmap操作完应该显示的释放
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return b;
    }
}
