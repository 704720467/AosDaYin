package com.zp.aosdayin.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.zp.aosdayin.R;

import java.util.ArrayList;
import java.util.List;


public class PrintUtil {

    /**
     * 打印
     *
     * @param bitmap
     */
    public static boolean toPrint(Context context, Bitmap bitmap, Handler handler) {
        if (bitmap == null)
            return false;
        boolean back = false;
        List<Bitmap> list = new ArrayList<Bitmap>();
        list.add(bitmap);
        int state = STPrinter.print(list, handler);
        switch (state) {
            case R.string.stprinter_print_param_error:
                break;
            case R.string.stprinter_connect_failed:
                if (STPrinter.openPrinter())
                    STPrinter.print(list, handler);
                break;
            case R.string.stprinter_print_failed:
                Toast.makeText(context, context.getResources().getString(state), Toast.LENGTH_SHORT).show();
                break;
            case R.string.stprinter_print_success:
                back = true;
                break;
        }
        bitmap.recycle();
        list.clear();
        list = null;
        return back;
    }

}
