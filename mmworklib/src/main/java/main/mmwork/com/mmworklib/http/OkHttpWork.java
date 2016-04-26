package main.mmwork.com.mmworklib.http;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhai on 15/11/30.
 */
public class OkHttpWork {

    private final static int TIME_OUT_MINUTES = 5000;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static OkHttpClient client;
    private static ConcurrentHashMap<WeakReference<Object>, ArrayList<Call>> callConcurrentHashMap = new ConcurrentHashMap<>();

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_MINUTES, TimeUnit.MINUTES)
                .readTimeout(TIME_OUT_MINUTES, TimeUnit.MINUTES)
                .writeTimeout(TIME_OUT_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    public static String get(Object tag, String url) {
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        try {
            Call call = client.newCall(request);
            addHttpWorkTag(tag, call);
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加用于cancel的网络标示
     *
     * @param tag
     * @param call
     */
    public synchronized static void addHttpWorkTag(Object tag, Call call) {
        if (null != tag) {
            ArrayList<Call> calls = getCallList(tag);
            if (null == calls) {
                //从未添加
                calls = new ArrayList<>();
            }
            calls.add(call);
            WeakReference<Object> weakReference = new WeakReference<>(tag);
            callConcurrentHashMap.put(weakReference, calls);
        }
    }

    public synchronized static void cancel(Object tag) {
        ArrayList<Call> calls = getCallList(tag);
        if (null != calls) {
            for (Call call : calls) {
                call.cancel();
            }
        }
    }

    public synchronized static ArrayList<Call> getCallList(Object tag) {
        if (null != tag) {
            Iterator iterator = callConcurrentHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<WeakReference<Object>, ArrayList<Call>> entry = (Map.Entry<WeakReference<Object>, ArrayList<Call>>) iterator.next();
                WeakReference<Object> weakReference = entry.getKey();
                if (null != weakReference.get() && tag == weakReference.get()) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public static String post(Object tag, String url, RequestBody formBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        try {
            Response response = null;
            Call call = client.newCall(request);
            addHttpWorkTag(tag, call);
            response = call.execute();
            response.networkResponse();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Call get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call post(String url, String json, Callback callback) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call downLoad(String url, final String filePath, final ProgressListener progressListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient cloneClient = newIterClient(client, progressListener);
        Call call = cloneClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressListener.onFailure(call,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    //将返回结果转化为流，并写入文件
                    int len;
                    byte[] buf = new byte[1024];
                    InputStream inputStream = null;

                    inputStream = response.body().byteStream();
                    //可以在这里自定义路径
                    File file1 = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file1);

                    while ((len = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return call;
    }


    public static void addIter(OkHttpClient cloneClient, final ProgressListener progressListener) {
        //添加拦截器，自定义ResponseBody，添加下载进度
        cloneClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
    }


    public static OkHttpClient newIterClient(OkHttpClient cloneClient, final ProgressListener progressListener) {
        //添加拦截器，自定义ResponseBody，添加下载进度
        return cloneClient.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build();
    }
}
