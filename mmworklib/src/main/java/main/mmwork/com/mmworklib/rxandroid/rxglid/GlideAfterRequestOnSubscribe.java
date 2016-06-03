package main.mmwork.com.mmworklib.rxandroid.rxglid;

import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import main.mmwork.com.mmworklib.rxandroid.MainThreadSubscription;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by zhai on 16/5/4.
 */
public class GlideAfterRequestOnSubscribe implements Observable.OnSubscribe<GlideBitmapDrawable> {

    final DrawableTypeRequest<?> typeRequest;
    final ImageView mView;

    GlideAfterRequestOnSubscribe(DrawableTypeRequest<?> typeRequest) {
        this.typeRequest = typeRequest;
        this.mView = null;
    }

    GlideAfterRequestOnSubscribe(DrawableTypeRequest<?> typeRequest, ImageView view) {
        this.typeRequest = typeRequest;
        this.mView = view;
    }

    @Override
    public void call(final Subscriber<? super GlideBitmapDrawable> subscriber) {

        final RequestListener requestListener = new RequestListener<Object, GlideBitmapDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                    if (null != mView) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean onResourceReady(GlideBitmapDrawable resource, Object model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(resource);
                    if (null != mView) {
                        return false;
                    }
                }
                return true;
            }
        };
        typeRequest.listener(requestListener);
        if (null != mView) {
            typeRequest.into(mView);
        } else {
            typeRequest.preload();
        }

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
            }
        });
    }
}
