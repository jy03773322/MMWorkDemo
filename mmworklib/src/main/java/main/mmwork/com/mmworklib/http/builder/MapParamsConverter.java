package main.mmwork.com.mmworklib.http.builder;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.Map;

import main.mmwork.com.mmworklib.http.OkHttpWork;

/**
 * Created by zhai on 16/1/18.
 */
public class MapParamsConverter {

    public static RequestBody map2ForBody(Map<String, String> map) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (map == null || map.size() == 0) return builder.build();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    public static RequestBody map2ForJSON(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject(map);
        RequestBody body = RequestBody.create(OkHttpWork.JSON, jsonObject.toString());
        return body;
    }


}
