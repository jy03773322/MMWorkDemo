package main.mmwork.com.mmworklib.rxandroid;

import java.util.concurrent.atomic.AtomicReference;

import main.mmwork.com.mmworklib.utils.Global;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by zhai on 16/6/14.
 */
public class MMSchedulers {
    private static final AtomicReference<MMSchedulers> INSTANCE = new AtomicReference<>();
    private final Scheduler workThreadScheduler;

    private static MMSchedulers getInstance() {
        for (; ; ) {
            MMSchedulers current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new MMSchedulers();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

    private MMSchedulers() {
        workThreadScheduler = AndroidSchedulers.from(Global.getWorkThreadLooper());
    }

    public static Scheduler workThread() {
        return getInstance().workThreadScheduler;
    }
}
