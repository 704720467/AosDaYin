package com.example.printerhelper;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
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

/**
 * 
 * @author HCOU
 * @date 2013-7-10
 */
public class QrCodeUtil {
	public static String VERTICAL = "vertical";
	public static String HORIZONTAL = "horizontal";

	/**
	 * 
	 * @date 2013-7-10
	 * @param str
	 * @param text_width
	 * @param text_height
	 * @param qr_width
	 * @param qr_height
	 * @param fangxiang
	 * @return
	 * @throws Exception
	 */
	public static Bitmap getCombineBitmap(String str, int text_width,
			int text_height, float textSize, int qr_width, int qr_height,
			int magin_type, String fangxiang, Context context) {
		try {
			return combineBitmap(
					creatCodeBitmap(str, text_width, text_height, textSize,
							context),
					createQRCode(str, qr_width, qr_height, magin_type, 0.0f),
					fangxiang, 0);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @date 2013-7-10
	 * @param str
	 * @param qr_width
	 * @param qr_height
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String str, int qr_width, int qr_height,
			int margin, float rangle) throws WriterException {
		int BLACK = 0xff000000;

		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, margin);
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		hints.put(EncodeHintType.MAX_SIZE, new Dimension(qr_width, qr_height));
		hints.put(EncodeHintType.MIN_SIZE, new Dimension(qr_width, qr_height));

		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, qr_width, qr_height, hints);

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

		Matrix m = new Matrix();
		m.setRotate(rangle);
		try {
			Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), m, true);
			if (bitmap != b2) {
				bitmap.recycle(); // Bitmap操作完应该显示的释放
				bitmap = b2;
			}
		} catch (OutOfMemoryError ex) {
		}

		return bitmap;
	}

	/**
	 * 生成编码的Bitmap
	 * 
	 * @param contents
	 * @param width
	 * @param height
	 * @param context
	 * @return
	 */
	public static Bitmap creatCodeBitmap(String contents, int width,
			int height, float textSize, Context context) {
		TextView tv = new TextView(context);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		String familyName = "����";
		Typeface font = Typeface.create(familyName, Typeface.NORMAL);

		tv.setTypeface(font, Typeface.NORMAL);
		tv.setLayoutParams(layoutParams);
		tv.setText(contents);
		tv.setHeight(height + 40);
		tv.setTextSize(textSize);
		tv.setGravity(Gravity.LEFT);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

		tv.buildDrawingCache();
		Bitmap bitmapCode = tv.getDrawingCache();
		return bitmapCode;
	}

	/**
	 * 
	 * @param background
	 * @param foreground
	 * @return Bitmap
	 */
	public static Bitmap combineBitmap(Bitmap background, Bitmap foreground,
			String fangxiang, float roate) {
		if (background == null) {
			return null;
		}
		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		int fgWidth = foreground.getWidth();
		int fgHeight = foreground.getHeight();

		Bitmap newmap = null;

		if (fangxiang.equals(QrCodeUtil.HORIZONTAL)) {
			newmap = Bitmap.createBitmap(bgWidth + fgWidth + 10, fgHeight,
					Config.ARGB_8888);
		} else if (fangxiang.equals(QrCodeUtil.VERTICAL)) {
			newmap = Bitmap.createBitmap(bgWidth + 20,
					bgHeight + fgHeight + 10, Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(newmap);
		if (fangxiang.equals(QrCodeUtil.HORIZONTAL)) {
			canvas.drawBitmap(background, 0, 0, null);
			canvas.drawBitmap(foreground, bgWidth + 10, 0, null);
		} else if (fangxiang.equals(QrCodeUtil.VERTICAL)) {
			canvas.drawBitmap(foreground, bgWidth / 2 - fgWidth / 2 - 20, 10,
					null);
			canvas.drawBitmap(background, 0, fgHeight + 15, null);
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		Matrix m = new Matrix();
		m.setRotate(roate);
		try {
			Bitmap b2 = Bitmap.createBitmap(newmap, 0, 0, newmap.getWidth(),
					newmap.getHeight(), m, true);
			if (newmap != b2) {
				newmap.recycle(); // Bitmap操作完应该显示的释放
				newmap = b2;
			}
		} catch (OutOfMemoryError ex) {
		}

		return newmap;
	}
}
