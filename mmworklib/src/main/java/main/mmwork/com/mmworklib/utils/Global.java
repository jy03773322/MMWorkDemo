package main.mmwork.com.mmworklib.utils;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by Zhai on 2016/1/22.
 */
public class Global {

    private static final String TAG = "Global";
    private static final String WORK_THREAD_NAME = "MMWorkThread";

    private static Looper mLooper;
    private static Handler mWorkHandler;
    private static Handler uiHandler;
    //判断是否在前台
    private static boolean isPageFont;
    //application上下文
    private static Context mContext;

    static {
        HandlerThread mThread = new HandlerThread(WORK_THREAD_NAME);
        mThread.start();
        mLooper = mThread.getLooper();
        mWorkHandler = new Handler(mLooper);
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public static Looper getWorkThreadLooper() {
        return mLooper;
    }

    /***
     * 获取全局变量
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        Global.mContext = mContext;
    }

    /***
     * 非UI线程调用耗时操作
     *
     * @param r
     */
    public static final void post2work(Runnable r) {
        mWorkHandler.post(r);
    }

    public static final void post2workDelay(Runnable r, long time) {
        mWorkHandler.postDelayed(r, time);
    }

    /***
     * Runnable post到主线程
     *
     * @param r
     */
    public static final void post2UI(Runnable r) {
        uiHandler.post(r);
    }

    public static final void postDelay2UI(Runnable r, long time) {
        uiHandler.postDelayed(r, time);
    }

    public static void removeRunnale(Runnable r) {
        if (null != r) {
            uiHandler.removeCallbacks(r);
            mWorkHandler.removeCallbacks(r);
        }
    }

    public static void removeCallbacksAndMessages() {
        uiHandler.removeCallbacksAndMessages(null);
        mWorkHandler.removeCallbacksAndMessages(null);
    }

    public static boolean isPageFont() {
        return isPageFont;
    }

    public static void setIsPageFont(boolean isPageFont) {
        Global.isPageFont = isPageFont;
    }
}
