package main.mmwork.com.mmworklib.bindingcollectionadapter;

/**
 * Created by zhai on 16/2/24.
 */
public class BindingUtils {

    public static boolean isBoolean(Boolean... bs) {
        for (Boolean b : bs) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

}
