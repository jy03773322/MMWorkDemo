package main.mmwork.com.mmworklib.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import main.mmwork.com.mmworklib.MMLibApplication;

public class MMDisplayUtils {

    private static int mScreenWidth;
    private static int mScreenHeight;

    public static int getScreenHeight(Context context) {
        WindowManager wm = ((Activity) context).getWindowManager();
        return wm.getDefaultDisplay().getHeight();
    }

    public static int getScreenWidth() {
        if (0 == mScreenWidth) {
            WindowManager wm = (WindowManager) MMLibApplication.context.getSystemService(Context.WINDOW_SERVICE);
            mScreenWidth = wm.getDefaultDisplay().getWidth();
        }
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        if (0 == mScreenHeight) {
            WindowManager wm = (WindowManager) MMLibApplication.context.getSystemService(Context.WINDOW_SERVICE);
            mScreenHeight = wm.getDefaultDisplay().getHeight();
        }
        return mScreenHeight;
    }

}

