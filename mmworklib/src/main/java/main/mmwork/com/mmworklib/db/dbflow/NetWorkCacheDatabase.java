package main.mmwork.com.mmworklib.db.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by zhai on 16/6/30.
 * 网络请求的缓存
 */
@Database(name = NetWorkCacheDatabase.NAME, version = NetWorkCacheDatabase.VERSION)
public class NetWorkCacheDatabase {

    public static final String NAME = "NetWorkCache";

    public static final int VERSION = 1;
}
