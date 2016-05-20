package main.mmwork.com.mmworklib;

import android.app.Activity;
import android.content.Context;

import main.mmwork.com.mmworklib.utils.DeviceInfo;
import main.mmwork.com.mmworklib.utils.DisplayUtil;
import main.mmwork.com.mmworklib.utils.FileUtils;

/**
 * Created by zhai on 16/5/8.
 */
public class MMWorkLibHellpter {

    /**
     * @param appContext
     * @param sdImagePath
     */
    public static void init(Context appContext, String sdImagePath) {
        FileUtils.initImageSdPath(sdImagePath);
    }

    /**
     * 在闪屏幕中的初始化
     *
     * @param activity
     */
    public static void splashInit(Activity activity) {
        DisplayUtil.init(activity);
        DeviceInfo.init(activity, activity);//初始化设备信息
    }

}
