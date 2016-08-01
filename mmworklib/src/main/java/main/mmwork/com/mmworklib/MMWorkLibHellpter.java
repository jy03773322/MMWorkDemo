package main.mmwork.com.mmworklib;

import android.content.Context;

import main.mmwork.com.mmworklib.utils.DeviceInfo;
import main.mmwork.com.mmworklib.utils.FileUtils;
import main.mmwork.com.mmworklib.utils.MMLogger;

/**
 * Created by zhai on 16/5/8.
 */
public class MMWorkLibHellpter {


    private volatile static MMWorkLibHellpter mInstance;

    public static MMWorkLibHellpter getInstance() {
        if (mInstance == null) {
            synchronized (MMWorkLibHellpter.class) {
                if (mInstance == null) {
                    mInstance = new MMWorkLibHellpter();
                }
            }
        }
        return mInstance;
    }

    private MMWorkLibHellpter() {

    }

    /**
     * @param sdImagePath
     */
    public MMWorkLibHellpter setSdCardImagePath(String sdImagePath) {
        FileUtils.initImageSdPath(sdImagePath);
        return this;
    }

    public MMWorkLibHellpter setIsSHowLog(String tag, boolean isSHowLog) {
        MMLogger.init(tag, isSHowLog);
        return this;
    }

    /**
     * 在闪屏幕中的初始化
     *
     * @param context
     */
    public MMWorkLibHellpter initDeviceInfo(Context context) {
        DeviceInfo.init(context);//初始化设备信息
        return this;
    }


}
