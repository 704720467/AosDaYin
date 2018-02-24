package com.zp.aosdayin.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.dothantech.lpapi.IAtBitmap.FontStyle;
import com.dothantech.lpapi.LPAPI;
import com.dothantech.lpapi.LPAPI.Callback;
import com.dothantech.lpapi.LPAPI.Factory;
import com.dothantech.printer.IDzPrinter.PrintProgress;
import com.dothantech.printer.IDzPrinter.PrinterAddress;
import com.dothantech.printer.IDzPrinter.PrinterInfo;
import com.dothantech.printer.IDzPrinter.PrinterState;
import com.dothantech.printer.IDzPrinter.ProgressInfo;
import com.zp.aosdayin.R;

import java.util.List;


public class STPrinter {
    private static String TAG = "STPrinter";
    private static Handler handler;

    public enum PrintType {
        // HuoBiao,
        // BiaoQian,
        XinXi;
    }

    private static LPAPI printer;
    private static PrinterAddress lastPrinter;

    private static void init() {
        if (printer == null) {
            printer = Factory.createInstance(new Callback() {

                @Override
                public void onStateChange(PrinterAddress arg0, PrinterState arg1) {
                    String back = "";
                    if (arg1 == PrinterState.Connecting)
                        back = "onStateChange返回PrinterState.Connecting";
                    if (arg1 == PrinterState.Connected)
                        back = "onStateChange返回PrinterState.Connected";
                    if (arg1 == PrinterState.Printing)
                        back = "onStateChange返回PrinterState.Printing";
                    if (arg1 == PrinterState.Disconnected)
                        back = "onStateChange返回PrinterState.Disconnected";
                    if (handler != null) {
                        Message message = handler.obtainMessage(2);
                        message.obj = back;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onProgressInfo(ProgressInfo arg0, Object arg1) {
                    String back = "";
                    if (arg0 == ProgressInfo.AdapterEnabling)
                        back = "onProgressInfo返回PrinterState.AdapterEnabling";
                    if (arg0 == ProgressInfo.AdapterEnabled)
                        back = "onProgressInfo返回PrinterState.AdapterEnabled";
                    if (arg0 == ProgressInfo.AdapterDisabled)
                        back = "onProgressInfo返回PrinterState.AdapterDisabled";
                    if (arg0 == ProgressInfo.DeviceBonding)
                        back = "onProgressInfo返回PrinterState.DeviceBonding";
                    if (arg0 == ProgressInfo.DeviceBonded)
                        back = "onProgressInfo返回PrinterState.DeviceBonded";
                    if (arg0 == ProgressInfo.DeviceUnbonded)
                        back = "onProgressInfo返回PrinterState.DeviceUnbonded";
                    if (handler != null) {
                        Message message = handler.obtainMessage(2);
                        message.obj = back;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onPrinterDiscovery(PrinterAddress arg0, PrinterInfo arg1) {
                    // 不在打印过程中
                    if (printer.getPrinterState() != PrinterState.Printing)
                        openPrinter(arg0);
                }

                @Override
                public void onPrintProgress(PrinterAddress arg0, Object arg1, PrintProgress arg2, Object arg3) {
                    String back = "";
                    if (arg2 == PrintProgress.Connected)
                        back = "onPrintProgress返回PrintProgress.Connected";
                    if (arg2 == PrintProgress.StartCopy)
                        back = "onPrintProgress返回PrintProgress.StartCopy";
                    if (arg2 == PrintProgress.Success)
                        back = "onPrintProgress返回PrintProgress.Success";
                    if (arg2 == PrintProgress.Failed)
                        back = "onPrintProgress返回PrintProgress.Failed";
                    if (handler != null) {
                        Message message = handler.obtainMessage(2);
                        message.obj = back;
                        handler.sendMessage(message);
                    }
                    //                    if (arg2 == PrintProgress.Success) {
                    //                        Log.i(TAG, "打印成功！");
                    //                        if (handler != null) {
                    //                            handler.sendEmptyMessage(2);
                    //                        }
                    //                    }
                }
            });
        }
    }

    /**
     * 打开默认打印机。
     */
    public static boolean openPrinter() {
        return openPrinter(null);
    }

    /**
     * 打开指定的打印机。
     *
     * @param address 目标打印机对象。
     * @return 成功与否。
     */
    public static boolean openPrinter(PrinterAddress address) {
        STPrinter.init();
        // 如果指定的打印机不为null，则保存并切换上次链接的打印机。
        if (address != null)
            lastPrinter = address;
        // 如果上次链接打印机为null，则将上次链接打印机设置为默认打印机。
        if (lastPrinter == null)
            lastPrinter = printer.getFirstPrinter();

        return printer.openPrinterByAddress(lastPrinter);
    }

    public static LPAPI getPrinter() {
        return printer;
    }

    public static void setPrinter(LPAPI printer) {
        STPrinter.printer = printer;
    }

    /**
     * 打印Bitmap列表。
     *
     * @param bmpList
     * @return
     */
    public static int print(List<Bitmap> bmpList, Handler handler) {
        // 检查打印参数。
        if (bmpList == null || bmpList.isEmpty())
            return R.string.stprinter_print_param_error;

        // 链接打印机。
        if (!openPrinter(null)) {
            return R.string.stprinter_connect_failed;
        }

        // 打印图片。
        for (Bitmap bitmap : bmpList) {
            if (!printBitmap(bitmap))
                return R.string.stprinter_print_failed;
        }

        return R.string.stprinter_print_success;
    }

    protected static boolean printBitmap(Bitmap bitmap) {
        if (bitmap == null)
            return false;

        double width = 48;
        double height = 30;

        printer.startJob(width, height, 0);
        printer.drawBitmap(bitmap, 0, 0, width, height);
        // printer.drawRectangle(0, 0, width, height, 0.5);

        return printer.commitJob();
    }

    public static Bitmap getBitmap(List<String> textList, PrintType printType) {
        if (printType == PrintType.XinXi)
            return getBitmap_XinXi(textList);

        return null;
    }

    public static Bitmap getBitmap_XinXi(List<String> textList) {
        double width = 48;
        double height = 30;
        double margin = 1.5;
        double lineHeight = (height - margin * 2) / 5;
        double fontHeight = 4.5;

        if (!printer.startJob(width, height, 0))
            return null;

        String content = "";
        if (textList.size() > 1) {
            content = "目的地:" + textList.get(1) + textList.get(2);
            printer.drawText(content, margin, margin + lineHeight * 0, width - margin * 2, lineHeight, fontHeight, FontStyle.BOLD);
        }
        if (textList.size() > 4) {
            content = "收货人:" + textList.get(4);
            printer.drawText(content, margin, margin + lineHeight * 1, width - margin * 2, lineHeight, fontHeight, FontStyle.BOLD);
        }
        if (textList.size() > 5) {
            content = "服务方式:" + textList.get(5);
            printer.drawText(content, margin, margin + lineHeight * 2, width - margin * 2, lineHeight, fontHeight, FontStyle.BOLD);
        }
        if (textList.size() > 6) {
            content = "发送包装:" + textList.get(6);
            printer.drawText(content, margin, margin + lineHeight * 3, width - margin * 2, lineHeight, fontHeight, FontStyle.BOLD);
        }
        if (textList.size() > 0) {
            content = "联络方式:" + textList.get(0);
            printer.drawText(content, margin, margin + lineHeight * 4, width - margin * 2, lineHeight, fontHeight, FontStyle.BOLD);
        }

        printer.endJob();
        List<Bitmap> pages = printer.getJobPages();
        if (pages != null && pages.size() > 0)
            return pages.get(0);

        return null;
    }
}
