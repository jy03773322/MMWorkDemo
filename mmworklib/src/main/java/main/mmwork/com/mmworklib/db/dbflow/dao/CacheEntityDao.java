package main.mmwork.com.mmworklib.db.dbflow.dao;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import main.mmwork.com.mmworklib.db.dbflow.entity.NetWorkRsultCacheEntity;
import main.mmwork.com.mmworklib.db.dbflow.entity.NetWorkRsultCacheEntity_Table;

/**
 * Created by zhai on 16/6/30.
 */

public class CacheEntityDao {

    public static CacheEntityDao get() {
        return new CacheEntityDao();
    }

    public NetWorkRsultCacheEntity selectForID(String url) {
       return SQLite.select()
                .from(NetWorkRsultCacheEntity.class)
                .where(NetWorkRsultCacheEntity_Table.url.eq(url))
                .querySingle();

    }

}
