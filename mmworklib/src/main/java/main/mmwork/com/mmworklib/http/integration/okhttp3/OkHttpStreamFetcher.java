package main.mmwork.com.mmworklib.http.integration.okhttp3;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import main.mmwork.com.mmworklib.utils.DeviceInfo;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Fetches an {@link InputStream} using the okhttp3library.
 */
public class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    final String REPLACE_HOST = "http://qiniu.xingyun.cn";
    final String HOST = "http://piccdn.xingyun.cn";

    private final Call.Factory client;
    private GlideUrl url;
    private InputStream stream;
    private ResponseBody responseBody;
    private volatile Call call;

    public OkHttpStreamFetcher(Call.Factory client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        Set<Map.Entry<String, String>> headerSet = url.getHeaders().entrySet();
        Response response;
        try {
            response = execute(headerSet);
        } catch (UnknownHostException e) {
            String replaceUrl = url.toStringUrl().replaceAll(HOST, REPLACE_HOST);
            url = new GlideUrl(replaceUrl);

            try {
                response = execute(headerSet);
            } catch (UnknownHostException hoste) {
                throw new UnknownHostException(hoste + "/" + url.toStringUrl() + "/" + DeviceInfo.getUuid());
            }

        }
        responseBody = response.body();
        if (!response.isSuccessful()) {
            throw new IOException("Request failed with  url:" + url.toStringUrl() + ",code:" + response.code());
        }

        long contentLength = responseBody.contentLength();
        stream = ContentLengthInputStream.obtain(responseBody.byteStream(), contentLength);
        return stream;
    }

    private Response execute(Set<Map.Entry<String, String>> headerSet) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : headerSet) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder.build();
        call = client.newCall(request);
        Response response = call.execute();
        return response;
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody.close();
        }
    }

    @Override
    public String getId() {
        return url.getCacheKey();
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
    }
}