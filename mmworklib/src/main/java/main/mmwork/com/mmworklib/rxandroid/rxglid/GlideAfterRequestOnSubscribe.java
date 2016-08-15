package main.mmwork.com.mmworklib.rxandroid.rxglid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * Created by zhai on 16/5/4.
 */
public class GlideAfterRequestOnSubscribe implements Observable.OnSubscribe<GlideBitmapDrawable> {

    final Context mContext;

    final DrawableTypeRequest<?> typeRequest;
    final ImageView mView;
    final int errorId;

    GlideAfterRequestOnSubscribe(Context context, DrawableTypeRequest<?> typeRequest) {
        this(context, typeRequest, null);
    }

    GlideAfterRequestOnSubscribe(Context context, DrawableTypeRequest<?> typeRequest, ImageView view) {
        this(context, typeRequest, view, 0);
    }

    GlideAfterRequestOnSubscribe(Context context, DrawableTypeRequest<?> typeRequest, ImageView view, int id) {
        this.mContext = context;
        this.typeRequest = typeRequest;
        this.mView = view;
        this.errorId = id;
    }

    @Override
    public void call(final Subscriber<? super GlideBitmapDrawable> subscriber) {

        final RequestListener requestListener = new RequestListener<Object, GlideBitmapDrawable>() {
            @Override
            public boolean onException(Exception e, Object model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                if (!subscriber.isUnsubscribed()) {
                    GlideBitmapDrawable glideBitmapDrawable = null;
                    if (0 != errorId) {
                        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), errorId);
                        glideBitmapDrawable = new GlideBitmapDrawable(mContext.getResources(), bitmap);
                        subscriber.onNext(glideBitmapDrawable);
                    }

                    if (null != mView) {
//                        if (null != glideBitmapDrawable) {
//                            mView.setImageBitmap(glideBitmapDrawable.getBitmap());
//                        }
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
