package main.mmwork.com.mmworklib.db.dbflow.entity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import main.mmwork.com.mmworklib.db.dbflow.NetWorkCacheDatabase;

/**
 * Created by zhai on 16/1/18.
 * 缓存实体类
 */
@Table(database = NetWorkCacheDatabase.class)
public class NetWorkRsultCacheEntity extends BaseModel {

    @PrimaryKey
    String url;

    @Column
    String resultJsonStr;

    boolean isCache = false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResultJsonStr() {
        return resultJsonStr;
    }

    public void setResultJsonStr(String resultJsonStr) {
        this.resultJsonStr = resultJsonStr;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }
}
