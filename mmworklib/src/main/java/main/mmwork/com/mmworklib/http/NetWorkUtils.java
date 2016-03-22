package main.mmwork.com.mmworklib.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/***
 * NetWork工具类
 * 获取APN_TYPE接入点类型
 */
public class NetWorkUtils {

    /**
     * 中国移动
     */
    public static final int OPERATOR_CHINA_MOBILE = 0;

    /**
     * 中国联通
     */
    public static final int OPERATOR_CHINA_UNICOM = 1;

    /**
     * 中国电信
     */
    public static final int OPERATOR_CHINA_TELECOM = 2;

    /**
     * 未知运营商
     */
    public static final int OPERATOR_UNKNOWN = -1;

    public static final int APN_WIFI = 0;

    public static final int APN_CMWAP = 1;

    public static final int APN_CMNET = 2;

    public static final int APN_UNIWAP = 3;

    public static final int APN_UNINET = 4;

    public static final int APN_3GWAP = 5;

    public static final int APN_3GNET = 6;

    public static final int APN_CTWAP = 7;

    public static final int APN_CTNET = 8;

    public static final String APN_WIFI_NAME = "wifi";

    public static final String APN_CMWAP_NAME = "cmwap";

    public static final String APN_CMNET_NAME = "cmnet";

    public static final String APN_UNIWAP_NAME = "uniwap";

    public static final String APN_UNINET＿NAME = "uninet";

    public static final String APN_3GWAP_NAME = "3gwap";

    public static final String APN_3GNET_NAME = "3gnet";

    public static final String APN_CTWAP_NAME = "ctwap";

    public static final String APN_CTNET_NAME = "ctnet";

    public static final int APN_UNKNOWN = -1;

    private static boolean isNetworkActive = true;

    public static boolean isNetworkWifi = false;

    /**
     * 获取移动网络接入点类型
     *
     * @param context
     * @return
     */
    private static int getMobileApnType(Context context) {
        final boolean isWap = isWap();
        final int operator = getSimOperator(context);
        switch (operator) {
            case OPERATOR_CHINA_MOBILE: // 中国移动
                if (isWap) {
                    return APN_CMWAP;
                } else {
                    return APN_CMNET;
                }
            case OPERATOR_CHINA_UNICOM: // 中国联通
                // 先判断是2g还是3g网络
                TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                final int networkType = telManager.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSDPA: // 联通3g
                        if (isWap) {
                            return APN_3GWAP;
                        } else {
                            return APN_3GNET;
                        }
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE: // 联通2g
                        if (isWap) {
                            return APN_UNIWAP;
                        } else {
                            return APN_UNINET;
                        }
                    default:
                        return APN_UNKNOWN;
                }
            case OPERATOR_CHINA_TELECOM: // 中国电信
                if (isWap) {
                    return APN_CTWAP;
                } else {
                    return APN_CTNET;
                }
            default:
                return APN_UNKNOWN;
        }
    }

    /**
     * 获取移动网络运营商
     *
     * @param context
     * @return
     */
    public static int getSimOperator(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telManager.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            if (networkOperator.equals("46000") || networkOperator.equals("46002") || networkOperator.equals("46007")) {
                return OPERATOR_CHINA_MOBILE;
            } else if (networkOperator.equals("46001")) {
                return OPERATOR_CHINA_UNICOM;
            } else if (networkOperator.equals("46003")) {
                return OPERATOR_CHINA_TELECOM;
            }
        }
        return OPERATOR_UNKNOWN;
    }

    /**
     * 判断是否是wap类网络
     *
     * @return
     */
    public static boolean isWap() {
        String host = Proxy.getDefaultHost();
        return !TextUtils.isEmpty(host);
    }

    /**
     * 获取接入点类型
     *
     * @return
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManager == null) {
            isNetworkActive = false;
            return APN_UNKNOWN;
        }
        NetworkInfo info = conManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            isNetworkActive = false;
            return APN_UNKNOWN;
        }
        isNetworkActive = true;
        int type = info.getType();
        if (type == ConnectivityManager.TYPE_WIFI) { // wifi网络
            isNetworkWifi = true;
            return APN_WIFI;
        } else {
            return getMobileApnType(context);
        }
    }

    public static final boolean isNetworkTypeWifi(Context context) {
        boolean flag = false;
        Context mContext = context;
        if (getNetworkType(mContext) == APN_WIFI) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取接入点名称
     *
     * @param context
     * @return
     */
    public static String getNetworkName(Context context) {
        final int netWorkType = getNetworkType(context);
        switch (netWorkType) {
            case NetWorkUtils.APN_WIFI:
                return NetWorkUtils.APN_WIFI_NAME;
            case NetWorkUtils.APN_CMWAP:
                return NetWorkUtils.APN_CMWAP_NAME;
            case NetWorkUtils.APN_CMNET:
                return NetWorkUtils.APN_CMNET_NAME;
            case NetWorkUtils.APN_3GWAP:
                return NetWorkUtils.APN_3GWAP_NAME;
            case NetWorkUtils.APN_3GNET:
                return NetWorkUtils.APN_3GNET_NAME;
            case NetWorkUtils.APN_UNIWAP:
                return NetWorkUtils.APN_UNIWAP_NAME;
            case NetWorkUtils.APN_UNINET:
                return NetWorkUtils.APN_UNINET＿NAME;
            case NetWorkUtils.APN_CTWAP:
                return NetWorkUtils.APN_CTWAP_NAME;
            case NetWorkUtils.APN_CTNET:
                return NetWorkUtils.APN_CTNET_NAME;
            default:
                return "NA";
        }
    }

    public static boolean isNetworkActive() {
        return isNetworkActive;
    }

    /**
     * 从Utils中剥离出
     * 判断网络是否可用
     *
     * @param context
     */
    public static boolean isNetworkAvailable(Context context) {
        return isNetworkActive();
    }
}
