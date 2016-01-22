package main.mmwork.com.mmworklib.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import main.mmwork.com.mmworklib.db.DatabaseHelper;
import main.mmwork.com.mmworklib.db.entity.NCacheEntity;


/**
 * Created by zhai on 16/1/20.
 */
public class CacheEntityDao {


    public static final String TAG = CacheEntityDao.class.getSimpleName();

    private Dao<NCacheEntity, String> mCacheDao;

    public CacheEntityDao(Context context) {
        try {
            mCacheDao = DatabaseHelper.getInstance(context).getDao(NCacheEntity.class);
        } catch (SQLException e) {
        }
    }

    public int delete(NCacheEntity user) {
        int cnt = 0;
        try {
            mCacheDao.delete(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnt;
    }

    public int saveItem(NCacheEntity user) {
        int cnt = 0;
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus = mCacheDao.createOrUpdate(user);
            cnt++;
        } catch (SQLException e) {
        }
        return cnt;
    }

    /**
     * @param url
     * @return
     */
    public NCacheEntity queryForID(String url) {
        try {
            return mCacheDao.queryForId(url);
        } catch (SQLException e) {
        }
        return null;
    }

    public List<NCacheEntity> queryForAll() {
        try {
            return mCacheDao.queryForAll();
        } catch (SQLException e) {
        }
        return null;
    }
}
