package main.mmwork.com.mmworklib.utils;

/**
 * Created by zhai on 16/5/18.
 */
public class MMLogger {

    public static void logd(String tag, Object... args) {
        com.orhanobut.logger.Logger.d(tag, args);
    }

    public static void logv(String tag, Object... args) {
        com.orhanobut.logger.Logger.v(tag, args);
    }

    public static void loge(String tag, Object... args) {
        com.orhanobut.logger.Logger.e(tag, args);
    }
}
