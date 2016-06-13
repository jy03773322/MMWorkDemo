package main.mmwork.com.mmworklib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

public class DensityUtility {

    private static final String TAG = null;

    /**
     * 根据手机的分辨率从dp 的单位 转成为px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px(像素) 的单位 转成为dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }


    @SuppressLint("DefaultLocale")
    public static int getValue(Context context) {
        String model = DeviceInfo.getMobileModel();
        int value = 0;
        if (!TextUtils.isEmpty(model)) {
            if (model.toLowerCase().equals("m355")) {
                value = DensityUtility.dip2px(context, 70.6f);
            } else if (model.toLowerCase().equals("mx4")) {
                value = DensityUtility.dip2px(context, 47);
            } else {
                int sw = MMDisplayUtils.getScreenWidth();
                if (sw <= 480) {
                    value = DensityUtility.dip2px(context, 15);
                } else if (sw == 720) {
                    value = DensityUtility.dip2px(context, 34);
                } else if (sw == 768) {
                    value = DensityUtility.dip2px(context, 47);
                } else if (sw == 1080) {
                    value = DensityUtility.dip2px(context, 35);
                } else {
                    value = DensityUtility.dip2px(context, 35);
                }
            }
        }
        return value;
    }

}