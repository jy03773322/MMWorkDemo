package main.mmwork.com.mmworklib.rxandroid.rxglid;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by zhai on 16/8/4.
 */
public class GlideThumnailAfterRequestOnSubscribe implements Observable.OnSubscribe<GlideBitmapDrawable> {

    final DrawableTypeRequest<?> typeRequest;

    GlideThumnailAfterRequestOnSubscribe(DrawableTypeRequest<?> typeRequest) {
        this.typeRequest = typeRequest;
    }

    @Override
    public void call(final Subscriber<? super GlideBitmapDrawable> subscriber) {

        final RequestListener requestListener = new RequestListener<Object, GlideBitmapDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(GlideBitmapDrawable resource, Object model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(resource);
                }
                return false;
            }
        };

        typeRequest.listener(requestListener);

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
            }
        });
    }

}
