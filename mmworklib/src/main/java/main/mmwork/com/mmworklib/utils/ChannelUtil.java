package main.mmwork.com.mmworklib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class ChannelUtil {

    private static final String CHANNEL_KEY = "UMENG_CHANNEL";

    private volatile static String channel;

    public static String getChannelCode(Context context) {
        if (TextUtils.isEmpty(channel)) {
            channel = getChannelCodeOnMeta(context);
        }
        return channel;
    }

    private static String getChannelCodeOnMeta(Context context) {
        String code = getMetaData(context, CHANNEL_KEY);
        if (!TextUtils.isEmpty(code)) {
            return code;
        }
        return null;
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
