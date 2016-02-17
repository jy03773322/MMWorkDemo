package main.mmwork.com.mmworklib.db.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by zhai on 16/1/18.
 * 缓存实体类
 */
@DatabaseTable
public class NetWorkRsultEntity implements Serializable {

    @DatabaseField(id = true)
    public String url;
    @DatabaseField
    public String resultJsonStr;

    public boolean isCache = false;

    public NetWorkRsultEntity() {
    }

}
