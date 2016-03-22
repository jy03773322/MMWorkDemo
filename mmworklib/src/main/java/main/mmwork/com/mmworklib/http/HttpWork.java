package main.mmwork.com.mmworklib.http;

import android.content.Context;
import android.text.TextUtils;

import main.mmwork.com.mmworklib.db.DatabaseHelper;
import main.mmwork.com.mmworklib.db.dao.CacheEntityDao;
import main.mmwork.com.mmworklib.db.entity.NetWorkRsultEntity;
import main.mmwork.com.mmworklib.http.builder.MapParamsConverter;
import main.mmwork.com.mmworklib.http.builder.ParamEntity;
import main.mmwork.com.mmworklib.http.builder.URLBuilder;
import main.mmwork.com.mmworklib.http.builder.URLBuilderFactory;
import main.mmwork.com.mmworklib.http.builder.URLBuilderHelper;
import main.mmwork.com.mmworklib.http.callback.NetworkCallback;
import main.mmwork.com.mmworklib.http.responser.AbstractResponser;
import main.mmwork.com.mmworklib.rxandroid.schedulers.AndroidSchedulers;
import okhttp3.Call;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhai on 15/12/1.
 * 网络请求类
 */
public class HttpWork {

    private final String TAG = HttpWork.class.getName();

    private CacheEntityDao cacheEntityDao;
    private Context context;

    public synchronized static HttpWork getInstace(Context context) {
        HttpWork mInstance = new HttpWork(context);
        return mInstance;
    }

    private HttpWork(Context context) {
        this.context = context;
        this.cacheEntityDao = new CacheEntityDao(context);
    }

    public <T extends AbstractResponser> Observable<T> get(ParamEntity paramEntity, final Class<T> rspClass, NetworkCallback<T> callback, boolean isNeedCache) {
        return req(paramEntity, rspClass, callback, false, isNeedCache);
    }

    public <T extends AbstractResponser> Observable<T> post(ParamEntity paramEntity, final Class<T> rspClass, NetworkCallback<T> callback, boolean isNeedCache) {
        return req(paramEntity, rspClass, callback, true, isNeedCache);
    }

    public <T extends AbstractResponser> Observable<T> req(ParamEntity paramEntity, final Class<T> rspClass, final NetworkCallback<T> callback, boolean isPost, boolean isNeedCache) {
        URLBuilder builder = URLBuilderFactory.build(paramEntity);
        final Observable<NetWorkRsultEntity> source;
        if (isNeedCache) {
            source = Observable.merge(reqCache(builder), reqNetWork(callback, builder, rspClass, isPost));
        } else {
            source = reqNetWork(callback, builder, rspClass, isPost);
        }
        final Observable<T> observable = source
//                .observeOn(Schedulers.io())
                .map(new Func1<NetWorkRsultEntity, T>() {
                    @Override
                    public T call(NetWorkRsultEntity s) {
                        T rsp = null;
                        try {
                            rsp = rspClass.newInstance();
                            rsp.parser(s.resultJsonStr);
                            rsp.isCache = s.isCache;
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        return rsp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        if (null != callback) {
                            if (t.isSuccess) {
                                callback.onSucessed(t);
                            } else if (!t.isCache) {
                                callback.onFailed(t.errorCode, t.errorMessage);
                            }
                        }
                    }
                });
        return observable;
    }

    /**
     * 查询网络数据
     *
     * @param builder
     */
    private <T extends AbstractResponser> Observable<NetWorkRsultEntity> reqNetWork(final Object tag, final URLBuilder builder, final Class<T> rspClass, final boolean isPost) {
        Observable<NetWorkRsultEntity> observable = Observable.create(new Observable.OnSubscribe<NetWorkRsultEntity>() {
            @Override
            public void call(Subscriber<? super NetWorkRsultEntity> subscriber) {
                String resultJsonStr = "";
                if (isPost) {
                    RequestBody body;
                    //post
                    if (builder.getisJson()) {
                        //JSON格式请求
                        body = MapParamsConverter.map2ForJSON(builder.getParams());
                    } else {
                        //KV格式请求
                        body = MapParamsConverter.map2ForBody(builder.getParams());
                    }
                    resultJsonStr = OkHttpWork.post(tag, builder.getUrl(), body);
                } else {
                    //get
                    String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getParams());
                    resultJsonStr = OkHttpWork.get(tag, urlKey);
                }
                NetWorkRsultEntity cacheEntity = new NetWorkRsultEntity();
                cacheEntity.resultJsonStr = resultJsonStr;
                cacheEntity.isCache = false;
                subscriber.onNext(cacheEntity);
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<NetWorkRsultEntity>() {
                    @Override
                    public void call(NetWorkRsultEntity entity) {
                        saveCache(builder, entity.resultJsonStr, rspClass);
                    }
                });
        return observable;
    }

    private <T extends AbstractResponser> void saveCache(URLBuilder builder, String s, Class<T> rspClass) {
        if (!TextUtils.isEmpty(s)) {
            try {
                T rsp = rspClass.newInstance();
                rsp.parseHeader(s);
                if (rsp.isSuccess) {
                    NetWorkRsultEntity entity = createCacheEntity(builder, s);
                    cacheEntityDao.saveItem(entity);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 查询本地DB缓存
     *
     * @param builder
     */
    private Observable<NetWorkRsultEntity> reqCache(final URLBuilder builder) {
        Observable<NetWorkRsultEntity> observable = Observable.create(new Observable.OnSubscribe<NetWorkRsultEntity>() {
            @Override
            public void call(Subscriber<? super NetWorkRsultEntity> subscriber) {
                String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getCacheKeyParams());
                NetWorkRsultEntity cacheEntity = cacheEntityDao.queryForID(urlKey);
                if (null == cacheEntity) {
                    cacheEntity = new NetWorkRsultEntity();
                }
                cacheEntity.isCache = true;
                subscriber.onNext(cacheEntity);
            }
        })
                .subscribeOn(Schedulers.io());
        return observable;
    }

    private NetWorkRsultEntity createCacheEntity(URLBuilder builder, String result) {
        NetWorkRsultEntity cacheEntity = new NetWorkRsultEntity();
        String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getCacheKeyParams());
        cacheEntity.url = urlKey;
        cacheEntity.resultJsonStr = result;
        return cacheEntity;
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        DatabaseHelper.getInstance(context).clearDb();
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param progressListener
     * @return
     */
    public Call downLoad(String url, String filePath, ProgressListener progressListener) {
        return OkHttpWork.downLoad(url, filePath, progressListener);
    }

    public static void cancel(Object... tags) {
        for (Object tag : tags) {
            OkHttpWork.cancel(tag);
        }
    }
}
