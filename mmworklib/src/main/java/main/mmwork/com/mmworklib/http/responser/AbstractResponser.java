package main.mmwork.com.mmworklib.http.responser;

import android.text.TextUtils;

import org.json.JSONObject;


/**
 * Created by zhai on 16/1/18.
 */
public abstract class AbstractResponser {

    private final String RET_CODE = "code";
    private final String RET_MSG = "desc";
    private final int SUCCESS_CODE = 0;
    private final String DEFAULT_ERROR_MESSAGE = "网络不稳定，请重试";

    public boolean isCache = false;
    public boolean isSuccess = false;
    public int errorCode = -1;
    public String errorMessage = DEFAULT_ERROR_MESSAGE;

    public void parser(final String result) {
        parseHeader(result);
        parserBody(result);
    }

    public boolean isCache() {
        return isCache;
    }

    public abstract void parserBody(final String result);

    public abstract String getErrorDesc(int errorCode);

    public JSONObject parseHeader(String result) {
        JSONObject dataObject = null;
        if (TextUtils.isEmpty(result)) {
            return dataObject;
        }
        try {
            dataObject = new JSONObject(result);
            isSuccess = isSuccess(dataObject);
            getErrorDesc(errorCode);
        } catch (Exception e) {
            errorCode = 0;
            isSuccess = true;
            e.printStackTrace();
        }
        return dataObject;
    }

    public boolean isSuccess(JSONObject dataObject) {
        if (null != dataObject) {
            errorCode = dataObject.optInt(RET_CODE, 0);
            errorMessage = dataObject.optString(RET_MSG, DEFAULT_ERROR_MESSAGE);
            if (SUCCESS_CODE == errorCode) {
                return true;
            }
        }
        return false;
    }
}
