package main.mmwork.com.mmworklib.rxandroid;

import android.os.Looper;

import java.util.concurrent.atomic.AtomicBoolean;

import main.mmwork.com.mmworklib.rxandroid.schedulers.AndroidSchedulers;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Created by zhai on 16/3/18.
 */
public abstract class MainThreadSubscription implements Subscription {
    /**
     * Verify that the calling thread is the Android main thread.
     * <p>
     * Calls to this method are usually preconditions for subscription behavior which instances of
     * this class later undo. See the class documentation for an example.
     *
     * @throws IllegalStateException when called from any other thread.
     */
    public static void verifyMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName());
        }
    }

    private final AtomicBoolean unsubscribed = new AtomicBoolean();

    @Override public final boolean isUnsubscribed() {
        return unsubscribed.get();
    }

    @Override public final void unsubscribe() {
        if (unsubscribed.compareAndSet(false, true)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                onUnsubscribe();
            } else {
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override public void call() {
                        onUnsubscribe();
                    }
                });
            }
        }
    }

    protected abstract void onUnsubscribe();
}
