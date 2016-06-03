package main.mmwork.com.mmworklib.utils;

/**
 * Created by zhai on 16/5/18.
 */
public class MMLogger {

    private static boolean isLog;

    public static void init(String tag,boolean isShowLog) {
        isLog = isShowLog;
        com.orhanobut.logger.Logger.init(tag);
    }

    public static void logd(String tag, Object... args) {
        if (isLog) {
            com.orhanobut.logger.Logger.d(tag, args);
        }
    }

    public static void logv(String tag, Object... args) {
        if (isLog) {
            com.orhanobut.logger.Logger.v(tag, args);
        }
    }

    public static void loge(String tag, Object... args) {
        if (isLog) {
            com.orhanobut.logger.Logger.e(tag, args);
        }
    }

    public static void logj(String json) {
        if (isLog) {
            com.orhanobut.logger.Logger.json(json);
        }
    }
}
