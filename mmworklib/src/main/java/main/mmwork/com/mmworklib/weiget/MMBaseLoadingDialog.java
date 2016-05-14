package main.mmwork.com.mmworklib.weiget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import main.mmwork.com.mmworklib.http.HttpWork;
import main.mmwork.com.mmworklib.utils.WeakArrayList;
import rx.Subscription;

/**
 * Created by zhai on 16/5/12.
 */
public class MMBaseLoadingDialog extends Dialog implements DialogInterface.OnDismissListener {

    private Subscription mSubscription;

    private WeakArrayList<Object> mNetworkTags = new WeakArrayList<>();

    public MMBaseLoadingDialog(Context context) {
        super(context);//默认是这个
        init();
    }

    public MMBaseLoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected MMBaseLoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    protected void init() {
        setOnDismissListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (null != mSubscription) {
            if (mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
                mSubscription = null;
            }
            Iterator<WeakReference> iterator = mNetworkTags.iterator();
            while (iterator.hasNext()) {
                WeakReference weakReference = iterator.next();
                if (null != weakReference.get()) {
                    HttpWork.cancel(weakReference.get());
                }
                iterator.remove();
            }
        }
    }
}
