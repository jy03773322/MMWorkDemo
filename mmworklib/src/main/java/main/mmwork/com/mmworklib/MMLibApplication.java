package main.mmwork.com.mmworklib;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhai on 16/5/9.
 */
public abstract class MMLibApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        MMWorkLibHellpter.initImageSdPath(sdPath());
    }

    public abstract String sdPath();
}
