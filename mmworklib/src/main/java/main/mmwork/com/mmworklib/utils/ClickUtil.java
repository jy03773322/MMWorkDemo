package main.mmwork.com.mmworklib.utils;

/**
 * Created by zhai on 16/6/29.
 */

public class ClickUtil {

    private static long lastClickTime;

    /**
     * 防止连点
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
