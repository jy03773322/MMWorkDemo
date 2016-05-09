package main.mmwork.com.mmworklib;

import android.app.Activity;

import main.mmwork.com.mmworklib.utils.DeviceInfo;
import main.mmwork.com.mmworklib.utils.DisplayUtil;
import main.mmwork.com.mmworklib.utils.FileUtils;

/**
 * Created by zhai on 16/5/8.
 */
public class MMWorkLibHellpter {

    public static void initImageSdPath(String sdPath) {
        FileUtils.initImageSdPath(sdPath);
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
