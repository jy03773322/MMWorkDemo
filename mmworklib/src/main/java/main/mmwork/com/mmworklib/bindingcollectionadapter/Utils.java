package main.mmwork.com.mmworklib.bindingcollectionadapter;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.lang.reflect.Field;


/**
 * Helper databinding utilities. May be made public some time in the future if they prove to be
 * useful.
 */
public class Utils {
    private static final String TAG = "BCAdapters";

    /**
     * Helper to throw an exception when {@link ViewDataBinding#setVariable(int,
     * Object)} returns false.
     */
    static void throwMissingVariable(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutRes) {
        Context context = binding.getRoot().getContext();
        Resources resources = context.getResources();
        String layoutName = resources.getResourceName(layoutRes);
        // Yeah reflection is slow, but this only happens when there is a programmer code.
        String bindingVariableName;
        try {
            bindingVariableName = getBindingVariableName(context, bindingVariable);
        } catch (Resources.NotFoundException e) {
            // Fall back to int
            bindingVariableName = "" + bindingVariable;
        }
        throw new IllegalStateException("Could not bind variable '" + bindingVariableName + "' in layout '" + layoutName + "'");
    }

    /**
     * Returns the name for the given binding variable int. Warning! This uses reflection so it
     * should <em>only</em> be used for debugging.
     *
     * @throws Resources.NotFoundException if the name cannot be found.
     */
    static String getBindingVariableName(Context context, int bindingVariable) throws Resources.NotFoundException {
        try {
            String packageName = context.getApplicationInfo().packageName;
            Class BRClass = Class.forName(packageName + ".BR");
            Field[] fields = BRClass.getFields();
            for (Field field : fields) {
                int value = field.getInt(null);
                if (value == bindingVariable) {
                    return field.getName();
                }
            }
        } catch (Exception e) {
            //Ignore, throws NotFoundException
        }
        throw new Resources.NotFoundException("" + bindingVariable);
    }

    /**
     * Ensures the call was made on the main thread. This is enforced for all ObservableList change
     * operations.
     */
    static void ensureChangeOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            throw new IllegalStateException("You must only modify the ObservableList on the main thread.");
        }
    }

    /**
     * Constructs a binding adapter class from it's class name using reflection.
     */
    @SuppressWarnings("unchecked")
    static <T, A extends BindingCollectionAdapter<T>> A createClass(String className, ItemViewArg<T> arg) {
        try {
            return (A) Class.forName(className).getConstructor(ItemViewArg.class).newInstance(arg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void addHeader(ListView parentView, ItemView header) {
        if (null != header) {
            LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
            View footerView = inflater.inflate(header.layoutRes(), null);
            parentView.addHeaderView(footerView);
        }
    }

//    static <H> void addHeader(GridViewWithHeaderAndFooter parentView,ItemView header,H entity) {
//        if (null != header) {
//            LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
//            ViewDataBinding binding = DataBindingUtil.inflate(inflater, header.layoutRes(), null, false);
//            if (null != entity) {
//                binding.setVariable(header.bindingVariable(), entity);
//            }
//            parentView.addHeaderView(binding.getRoot());
//        }
//    }
//
//    static void addFooter(GridViewWithHeaderAndFooter parentView, ItemView footer) {
//        if (null != footer) {
//            LayoutInflater inflater = LayoutInflater.from(parentView.getContext());
//            View footerView = inflater.inflate(footer.layoutRes(), null);
//            parentView.addFooterView(footerView);
//        }
//    }
}
