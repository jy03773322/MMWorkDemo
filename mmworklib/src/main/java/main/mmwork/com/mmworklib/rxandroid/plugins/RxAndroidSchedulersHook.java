package main.mmwork.com.mmworklib.rxandroid.plugins;

import rx.Scheduler;
import rx.functions.Action0;

public class RxAndroidSchedulersHook {
    private static final RxAndroidSchedulersHook DEFAULT_INSTANCE = new RxAndroidSchedulersHook();

    public static RxAndroidSchedulersHook getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public Scheduler getMainThreadScheduler() {
        return null;
    }

    /**
     * Invoked before the Action is handed over to the scheduler.  Can be used for
     * wrapping/decorating/logging. The default is just a passthrough.
     *
     * @param action action to schedule
     * @return wrapped action to schedule
     */
    public Action0 onSchedule(Action0 action) {
        return action;
    }
}
