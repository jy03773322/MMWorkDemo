package main.mmwork.com.mmworklib.http;

import android.content.Context;
import android.text.TextUtils;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.RequestBody;

import main.mmwork.com.mmworklib.db.dao.CacheEntityDao;
import main.mmwork.com.mmworklib.db.entity.NCacheEntity;
import main.mmwork.com.mmworklib.http.builder.MapParamsConverter;
import main.mmwork.com.mmworklib.http.builder.ParamEntity;
import main.mmwork.com.mmworklib.http.builder.URLBuilder;
import main.mmwork.com.mmworklib.http.builder.URLBuilderFactory;
import main.mmwork.com.mmworklib.http.builder.URLBuilderHelper;
import main.mmwork.com.mmworklib.http.callback.NetworkCallback;
import main.mmwork.com.mmworklib.http.responser.AbstractResponser;
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

    private static volatile HttpWork mInstance;

    private CacheEntityDao cacheEntityDao;
    private Context context;

    public static HttpWork getInstace(Context context) {
        if (null == mInstance) {
            synchronized (HttpWork.class) {
                if (null == mInstance) {
                    mInstance = new HttpWork(context);
                }
            }
        }
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
        final Observable<String> source;
        if (isNeedCache) {
            source = Observable.merge(reqCache(builder), reqNetWork(callback, builder, rspClass, isPost));
        } else {
            source = reqNetWork(callback, builder, rspClass, isPost);
        }
        final Observable<T> observable = source
                .map(new Func1<String, T>() {
                    @Override
                    public T call(String s) {
                        T rsp = null;
                        try {
                            rsp = rspClass.newInstance();
                            rsp.parser(s);
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
                            } else {
                                callback.onFilled(t.errorCode, t.errorMessage);
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
    private <T extends AbstractResponser> Observable<String> reqNetWork(final Object tag, final URLBuilder builder, final Class<T> rspClass, final boolean isPost) {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
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
                subscriber.onNext(resultJsonStr);
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        saveCache(builder, s, rspClass);
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
                    NCacheEntity entity = createCacheEntity(builder, s);
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
    private Observable<String> reqCache(final URLBuilder builder) {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String resultJsonStr = null;
                String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getParams());
                NCacheEntity cacheEntity = cacheEntityDao.queryForID(urlKey);
                if (null != cacheEntity) {
                    resultJsonStr = cacheEntity.resultJsonStr;
                }
                subscriber.onNext(resultJsonStr);
            }
        })
                .subscribeOn(Schedulers.io());
        return observable;
    }

    private NCacheEntity createCacheEntity(URLBuilder builder, String result) {
        NCacheEntity cacheEntity = new NCacheEntity();
        String urlKey = URLBuilderHelper.getUrlStr(builder.getUrl(), builder.getParams());
        cacheEntity.url = urlKey;
        cacheEntity.resultJsonStr = result;
        return cacheEntity;
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

    public void cancel(Object tag) {
        OkHttpWork.cancel(tag);
    }
}
