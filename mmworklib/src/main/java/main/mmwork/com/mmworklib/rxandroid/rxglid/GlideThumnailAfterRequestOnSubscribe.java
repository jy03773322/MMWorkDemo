package main.mmwork.com.mmworklib.rxandroid.rxglid;

import android.widget.ImageView;

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

    final DrawableTypeRequest<?> imageRequest;
    final DrawableTypeRequest<?> thumbnailRequest;

    final ImageView mImageView;

    GlideThumnailAfterRequestOnSubscribe(DrawableTypeRequest<?> request, DrawableTypeRequest<?> typeRequest, ImageView imageView) {
        this.imageRequest = request;
        this.thumbnailRequest = typeRequest;
        this.mImageView = imageView;
    }

    @Override
    public void call(final Subscriber<? super GlideBitmapDrawable> subscriber) {

        final RequestListener requestListener = new RequestListener<Object, GlideBitmapDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                    if (null != mImageView) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public boolean onResourceReady(GlideBitmapDrawable resource, Object model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(resource);
                    if (null != mImageView) {
                        return false;
                    }
                }
                return true;
            }
        };

        thumbnailRequest.listener(requestListener);
        imageRequest
                .thumbnail(thumbnailRequest);

        if (null != mImageView) {
            imageRequest.into(mImageView);
        } else {
            imageRequest.preload();
        }

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
            }
        });
    }

}
