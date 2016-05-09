package main.mmwork.com.mmworklib.utils;

import android.databinding.BaseObservable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zhai on 16/3/3.
 * 反射赋值
 */
public class EntityUtils {

    public static <T extends Object> void resolveAllFieldsSet(final T object, T resultObject) {
        if (null == object || null == resultObject) {
            return;
        }
        setFieldValue(object, getFieldValueMap(resultObject));
    }

    public static <T extends Object> Object resolveAllFields(Class cls, T object, T resultObject) {
        if (cls != null && !cls.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            if (fields != null) {
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName();
                    if (!name.startsWith("this$")) {
                        fields[i].setAccessible(true);
                        try {
                            Object v = fields[i].get(resultObject);
                            fields[i].set(object, v);
                        } catch (IllegalArgumentException ee) {
                            Log.e("EntityHellper", ee.getMessage());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (object instanceof BaseObservable) {
            ((BaseObservable) object).notifyChange();
        }
        return object;
    }

    public static Object resolveAllFields(Class cls, Object object, Class resultcls, Object resultObject) throws IllegalAccessException {
        if (cls != null && !cls.equals(Object.class) && null != resultcls && !resultObject.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            Field[] resultFields = resultcls.getDeclaredFields();

            if (fields != null && resultFields != null) {
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName();
                    if (!name.startsWith("this$")) {
                        for (Field resultField : resultFields) {
                            fields[i].setAccessible(true);
                            String resultName = resultField.getName();
                            if (name.equals(resultName)) {
                                Object v = resultField.get(resultObject);
                                fields[i].set(object, v);
                            }
                        }

                    }
                }
            }
        }
        if (object instanceof BaseObservable) {
            ((BaseObservable) object).notifyChange();
        }
        return object;
    }

    /**
     * 取Bean的属性和值对应关系的MAP
     *
     * @param bean
     * @return Map
     */
    public static Map<String, Object> getFieldValueMap(Object bean) {
        Class<?> cls = bean.getClass();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldType = field.getType().getSimpleName();
                String fieldGetName = parGetName(field.getName());
                if (!checkGetMet(methods, fieldGetName)) {
                    continue;
                }
                Method fieldGetMet = cls.getMethod(fieldGetName, new Class[]{});
                Object fieldVal = fieldGetMet.invoke(bean, new Object[]{});
                valueMap.put(field.getName(), fieldVal);
            } catch (Exception e) {
                continue;
            }
        }
        return valueMap;
    }

    /**
     * set属性的值到Bean
     *
     * @param bean
     * @param valMap
     */
    private static void setFieldValue(Object bean, Map<String, Object> valMap) {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            try {
                String fieldSetName = parSetName(field.getName());
//                if (!checkSetMet(methods, fieldSetName)) {
//                    continue;
//                }
                Method fieldSetMet = cls.getMethod(fieldSetName,
                        field.getType());
//              String fieldKeyName = parKeyName(field.getName());
                String fieldKeyName = field.getName();
                Object value = valMap.get(fieldKeyName);
                fieldSetMet.invoke(bean, value);
                //get方法比对赋值,用不上
//                String fieldGetName = parGetName(field.getName());
//                Method fieldGetMet = cls.getMethod(fieldGetName, null);
//                Object oldValue = fieldGetMet.invoke(bean, null);
//                if (oldValue!=value){
//                    fieldSetMet.invoke(bean, value);
//                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 格式化string为Date
     *
     * @param datestr
     * @return date
     */
    public static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
        try {
            String fmtstr = null;
            if (datestr.indexOf(':') > 0) {
                fmtstr = "yyyy-MM-dd HH:mm:ss";
            } else {
                fmtstr = "yyyy-MM-dd";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return (Date) sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期转化为String
     *
     * @param date
     * @return date string
     */
    public static String fmtDate(Date date) {
        if (null == date) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否存在某属性的 set方法
     *
     * @param methods
     * @param fieldSetMet
     * @return boolean
     */
    public static boolean checkSetMet(Method[] methods, String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在某属性的 get方法
     *
     * @param methods
     * @param fieldGetMet
     * @return boolean
     */
    public static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接某属性的 get方法
     *
     * @param fieldName
     * @return String
     */
    public static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "get"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 拼接在某属性的 set方法
     *
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        int startIndex = 0;
        if (fieldName.charAt(0) == '_')
            startIndex = 1;
        return "set"
                + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
                + fieldName.substring(startIndex + 1);
    }

    /**
     * 获取存储的键名称（调用parGetName）
     *
     * @param fieldName
     * @return 去掉开头的get
     */
    public static String parKeyName(String fieldName) {
        String fieldGetName = parGetName(fieldName);
        if (!TextUtils.isEmpty(fieldGetName) && TextUtils.isEmpty(fieldGetName.trim())
                && fieldGetName.length() > 3) {
            return fieldGetName.substring(3);
        }
        return fieldGetName;
    }

}
