package main.mmwork.com.mmworklib.http.builder;

import com.squareup.okhttp.FormEncodingBuilder;

import java.util.Map;

/**
 * Created by zhai on 16/1/18.
 */
public class MapParamsConverter {

    public static FormEncodingBuilder map2FormEncodingBuilder(Map<String, String> map) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (map == null || map.size() == 0) return builder;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                builder.add(key, value);
            }
        }
        return builder;
    }
}
