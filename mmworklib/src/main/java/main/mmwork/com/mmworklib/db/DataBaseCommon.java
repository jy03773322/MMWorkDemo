package main.mmwork.com.mmworklib.db;


import java.util.HashMap;
import java.util.Map;

import main.mmwork.com.mmworklib.db.entity.NetWorkRsultEntity;


/**
 * Created by diaosi on 2015/12/7.
 */
public class DataBaseCommon {

    //http缓存数据库
    public static final String HTTP_CACHE_TABLE = "httpCacheTable";

    //db名称
    public static final String DB_NAME = "httpcache.db";
    //db version
    public static final int DB_VERSION = 2;

    public static final Map<Class<?>, TableInfo> mMap = new HashMap<Class<?>, TableInfo>();

    static {
        //table TimeLineDataTable version 1
        mMap.put(NetWorkRsultEntity.class, new TableInfo(HTTP_CACHE_TABLE, 2));
    }
}
