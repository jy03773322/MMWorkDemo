package main.mmwork.com.mmworklib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import main.mmwork.com.mmworklib.rxandroid.MMSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class ChannelUtil {
    private static final String TAG = "ChannelUtil";

    private static final String CHANNEL_KEY = "mmchannel";

    private static final String CHANNEL_DEFAULT = "dev";

    private volatile static String channel;


    public static String getChannel(Context context) {
        if (TextUtils.isEmpty(channel)) {
            channel = getChannelBySharedPreferences(context, CHANNEL_KEY);
            if (TextUtils.isEmpty(channel)) {
                channel = getChannelFromApk(context, CHANNEL_KEY);
                if (TextUtils.isEmpty(channel)) {
                    channel = CHANNEL_DEFAULT;
                }
                saveChannelBySharedPreferences(context, channel);
            }
        }
        return channel;
    }

    /**
     * 获得channelCode
     *
     * @return
     */
    public static Observable<String> getChannelObservable(Context context) {
        return Observable.concat(getChannelCodeByLocalObservable(), getChannelBySpObservable(context), getChannelCodeByFileObservable(context))
                .first(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        return CHANNEL_DEFAULT;
                    }
                });
    }


    private static Observable<String> getChannelByMeta(final Context context) {
        return Observable.just(CHANNEL_KEY)
                .subscribeOn(MMSchedulers.workThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String channelKey) {
                        String channel = getChannelCodeOnMeta(context, channelKey);
                        return channel;
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        channel = s;
                    }
                });
    }

    /**
     * 直接从内存获取
     *
     * @return
     */
    private static Observable<String> getChannelCodeByLocalObservable() {
        return Observable.just(channel);
    }

    /**
     * 从APK中读取
     *
     * @param context
     * @return
     */
    private static Observable<String> getChannelCodeByFileObservable(final Context context) {
        return Observable.just(CHANNEL_KEY)
                .subscribeOn(MMSchedulers.workThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String key) {
                        return getChannelFromApk(context, key);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        saveChannelBySharedPreferences(context, s);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        channel = s;
                    }
                });
    }

    /**
     * 从apk中获取版本信息
     *
     * @param context
     * @param channelKey
     * @return
     */
    private static String getChannelFromApk(Context context, String channelKey) {
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/" + channelKey;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        Log.d(TAG, "getChannelFromApk: channel:" + channel);
        return channel;
    }

    public static Observable<String> getChannelBySpObservable(final Context context) {
        return Observable.just(CHANNEL_KEY)
                .subscribeOn(MMSchedulers.workThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return getChannelBySharedPreferences(context, s);
                    }
                })
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        channel = s;
                    }
                });
    }

    /**
     * 从sp中获取channel
     *
     * @param context
     * @return 为空表示获取异常、sp中的值已经失效、sp中没有此值
     */
    private static String getChannelBySharedPreferences(Context context, String channelKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(channelKey, "");
    }

    /**
     * 本地保存channel & 对应版本号
     *
     * @param context
     * @param channel
     */
    private static void saveChannelBySharedPreferences(Context context, String channel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CHANNEL_KEY, channel);
        editor.apply();
    }

    private static String getChannelCodeOnMeta(Context context, String key) {
        String code = getMetaData(context, key);
        if (!TextUtils.isEmpty(code)) {
            return code;
        }
        return null;
    }

    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}
