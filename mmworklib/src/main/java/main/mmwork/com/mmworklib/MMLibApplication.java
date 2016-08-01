package main.mmwork.com.mmworklib;

import android.app.Application;
import android.content.Context;

import main.mmwork.com.mmworklib.utils.Global;

/**
 * Created by zhai on 16/5/9.
 */
public abstract class MMLibApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Global.setContext(getApplicationContext());
        MMWorkLibHellpter.getInstance()
                .initDeviceInfo(Global.getContext());
        Global.post2work(new Runnable() {
            @Override
            public void run() {
                MMWorkLibHellpter.getInstance()
                        .setSdCardImagePath(initSDCardImagePath())
                        .setIsSHowLog(initLogTag(), initIsLog());
            }
        });
    }

    public abstract String initSDCardImagePath();

    public abstract String initLogTag();

    public abstract boolean initIsLog();
}