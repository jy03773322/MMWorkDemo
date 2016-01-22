package main.mmwork.com.mmworklib.http.responser;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhai on 16/1/18.
 */
public abstract class AbstractResponser {

    private final String RET_CODE = "code";
    private final String RET_MSG = "desc";
    private final int SUCCESS_CODE = 0;

    public boolean isSuccess = false;
    public int errorCode = -1;
    public String errorMessage;

    public void parser(final String result) {
        parseHeader(result);
        parserBody(result);
    }

    public abstract void parserBody(final String result);

    public abstract String getErrorDesc(int errorCode);

    public JSONObject parseHeader(String result) {
        JSONObject dataObject = null;
        if (TextUtils.isEmpty(result)) {
            return dataObject;
        }
        try {
            dataObject = JSON.parseObject(result);
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
            errorCode = 0;
            isSuccess = true;
            return true;
        }
        errorCode = dataObject.getIntValue(RET_CODE);
        errorMessage = dataObject.getString(RET_MSG);
        if (SUCCESS_CODE == errorCode) {
            return true;
        } else {
            return false;
        }
    }
}
