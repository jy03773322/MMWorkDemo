package main.mmwork.com.mmworklib.http.builder;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhai on 15/4/27.
 */
public class DefaultURLBuilder implements URLBuilder {

    private String url;
    private Map<String, String> paramsMap;

    @Override
    public void parse(Path path, Map<String, Field> fields,
                      ParamEntity entity) throws IllegalAccessException {
        url = path.host() + path.url();
        paramsMap = new HashMap<String, String>();
        //增加通用参数
        addCommonParams(paramsMap);
        if (fields != null) {
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                Object value = entry.getValue().get(entity);
                if (value != null) {
                    paramsMap.put(entry.getKey(), String.valueOf(value));
                }
            }
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Map<String, String> getParams() {
        return paramsMap;
    }

    @Override
    public boolean getisJson() {
        return false;
    }

    /**
     * 填入通用参数
     *
     * @param tempParams
     */
    public void addCommonParams(Map<String, String> tempParams) {
    }
}
