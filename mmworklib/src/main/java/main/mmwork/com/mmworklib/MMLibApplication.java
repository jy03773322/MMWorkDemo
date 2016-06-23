package main.mmwork.com.mmworklib;

import android.app.Application;

import main.mmwork.com.mmworklib.utils.Global;

/**
 * Created by zhai on 16/5/9.
 */
public abstract class MMLibApplication extends Application {

    private static MMLibApplication sApplication;

    public static MMLibApplication getInstance() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Global.setContext(getApplicationContext());
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
