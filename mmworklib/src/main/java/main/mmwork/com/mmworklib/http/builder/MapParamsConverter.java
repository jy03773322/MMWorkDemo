package main.mmwork.com.mmworklib.http.builder;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import main.mmwork.com.mmworklib.http.OkHttpWork;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zhai on 16/1/18.
 */
public class MapParamsConverter {

    public static RequestBody map2ForBody(Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map == null || map.size() == 0) return builder.build();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            if (key != null && value != null) {
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    public static RequestBody map2ForJSON(Map<String, Object> map) {
        Gson gson = new GsonBuilder().create();
        String jsonStirng = gson.toJson(map);
        RequestBody body = RequestBody.create(OkHttpWork.JSON, jsonStirng);
        return body;
    }


}
