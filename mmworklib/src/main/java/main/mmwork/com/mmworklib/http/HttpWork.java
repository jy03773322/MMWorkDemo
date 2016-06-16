package main.mmwork.com.mmworklib.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
import main.mmwork.com.mmworklib.http.upload.ProgressHelper;
import main.mmwork.com.mmworklib.http.upload.UIProgressListener;
import main.mmwork.com.mmworklib.utils.MMLogger;
import okhttp3.Call;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
        return req(paramEntity, rspClass, callback, null, false, isNeedCache);
    }

    public <T extends AbstractResponser> Observable<T> post(ParamEntity paramEntity, final Class<T> rspClass, NetworkCallback<T> callback, boolean isNeedCache) {
        return req(paramEntity, rspClass, callback, null, true, isNeedCache);
    }

    public <T extends AbstractResponser> Observable<T> post(ParamEntity paramEntity, final Class<T> rspClass, NetworkCallback<T> callback, final UIProgressListener uiProgressRequestListener, boolean isNeedCache) {
        return req(paramEntity, rspClass, callback, uiProgressRequestListener, true, isNeedCache);
    }

    public <T extends AbstractResponser> Observable<T> req(ParamEntity paramEntity, final Class<T> rspClass, final NetworkCallback<T> callback, final UIProgressListener uiProgressRequestListener, final boolean isPost, final boolean isNeedCache) {
        return Observable.just(paramEntity)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<ParamEntity, URLBuilder>() {
                    @Override
                    public URLBuilder call(ParamEntity paramEntity) {
                        URLBuilder builder = URLBuilderFactory.build(paramEntity);
                        return builder;
                    }
                })
                .flatMap(new Func1<URLBuilder, Observable<T>>() {
                    @Override
                    public Observable<T> call(URLBuilder urlBuilder) {
                        return reqOKhttp(urlBuilder, rspClass, callback, uiProgressRequestListener, isPost, isNeedCache);
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
    }

    private <T extends AbstractResponser> Observable<T> reqOKhttp(URLBuilder builder, final Class<T> rspClass,
                                                                  final NetworkCallback<T> callback,
                                                                  final UIProgressListener uiProgressRequestListener, boolean isPost, boolean isNeedCache) {
        final Observable<NetWorkRsultEntity> source;
        if (isNeedCache) {
            source = Observable.merge(reqCache(builder), reqNetWork(callback, builder, rspClass, uiProgressRequestListener, isPost, isNeedCache));
        } else {
            source = reqNetWork(callback, builder, rspClass, uiProgressRequestListener, isPost, isNeedCache);
        }
        final Observable<T> observable = source
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
                });
        return observable;
    }


    /**
     * 查询网络数据
     *
     * @param builder
     */
    private <T extends AbstractResponser> Observable<NetWorkRsultEntity> reqNetWork(final Object tag, final URLBuilder builder, final Class<T> rspClass, final UIProgressListener uiProgressRequestListener, final boolean isPost, final boolean isCache) {
        Observable<NetWorkRsultEntity> observable = Observable.create(new Observable.OnSubscribe<NetWorkRsultEntity>() {
            @Override
            public void call(Subscriber<? super NetWorkRsultEntity> subscriber) {
                String resultJsonStr = "";
                if (isPost) {
                    RequestBody body = null;
                    //post
                    if (URLBuilder.REQ_TYPE_JSON == builder.getReqType()) {
                        //JSON格式请求
                        body = MapParamsConverter.map2ForJSON(builder.getParams());
                    } else if (URLBuilder.REQ_TYPE_KV == builder.getReqType()) {
                        //KV格式请求
                        body = MapParamsConverter.map2ForBody(builder.getParams());
                    } else if (URLBuilder.REQ_TYPE_FILE == builder.getReqType()) {
                        //KV格式请求
                        body = MapParamsConverter.map2ForMultBody(builder.getParams());
                        if (null != uiProgressRequestListener) {
                            //判断是否有上传进度listener
                            body = ProgressHelper.addProgressRequestListener(body, uiProgressRequestListener);
                        }
                    }
                    Log.d(TAG, "call: body"+body.toString());

                    resultJsonStr = OkHttpWork.post(tag, builder.getUrl(), body);
                } else {
                    //get
                    String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getParams());
                    resultJsonStr = OkHttpWork.get(tag, urlKey);
                }
                NetWorkRsultEntity cacheEntity = new NetWorkRsultEntity();
                cacheEntity.resultJsonStr = resultJsonStr;
                cacheEntity.isCache = false;
                Log.d(TAG, "reqNetWork: "+cacheEntity.resultJsonStr);
                subscriber.onNext(cacheEntity);
            }
        })
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<NetWorkRsultEntity>() {
                    @Override
                    public void call(NetWorkRsultEntity entity) {
                        if (isCache) {
                            saveCache(builder, entity.resultJsonStr, rspClass);
                        }
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
                MMLogger.logv(TAG, "reqCache: " + cacheEntity.resultJsonStr);
                subscriber.onNext(cacheEntity);
            }
        })
                .subscribeOn(Schedulers.computation());
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
