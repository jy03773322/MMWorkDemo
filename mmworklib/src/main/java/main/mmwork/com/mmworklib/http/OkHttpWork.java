package main.mmwork.com.mmworklib.http;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhai on 15/11/30.
 */
public class OkHttpWork {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static OkHttpClient client;
    private static ConcurrentHashMap<WeakReference<Object>, ArrayList<Call>> callConcurrentHashMap = new ConcurrentHashMap<>();

    static {
        client = new OkHttpClient();
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

    public static String post(Object tag, String url, FormEncodingBuilder builder) {
        RequestBody formBody = builder.build();
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
            return response.networkResponse().body().string();
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

    public static Call downLoad(String url, final String filePath, ProgressListener progressListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient cloneClient = client.clone();
        addIter(cloneClient, progressListener);
        Call call = cloneClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("failure");
            }

            @Override
            public void onResponse(Response response) {
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
        cloneClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
    }


}
