package main.mmwork.com.mmworklib.rxandroid.rxglid;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by zhai on 16/5/4.
 */
public class RxGlide {

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(Context context, DrawableTypeRequest<?> request) {
        return afterGlideRequestListener(context, request, 0);

    }

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(Context context, DrawableTypeRequest<?> request, int errorId) {
        return afterGlideRequestListener(context, request, null, errorId);
    }

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(Context context, DrawableTypeRequest<?> request, ImageView imageView) {
        return afterGlideRequestListener(context, request, imageView, 0);
    }

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(Context context, DrawableTypeRequest<?> request, ImageView imageView, int errorId) {
        if (null == request) {
            return Observable.empty();
        }
        if (0 != errorId) {
            request.error(errorId);
        }
        return Observable.create(new GlideAfterRequestOnSubscribe(context, request, imageView, errorId))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param context
     * @param request
     * @param thumbnailRequest
     * @param imageView
     * @param errorId          没有传0
     * @return
     */
    public static Observable<GlideBitmapDrawable> afterGlideThumnailRequestListener(Context context, DrawableTypeRequest<?> request, DrawableTypeRequest<?> thumbnailRequest, ImageView imageView, int errorId) {
        if (null == request) {
            return Observable.empty();
        }
        if (0 != errorId) {
            request.error(errorId);
        }
        Observable<GlideBitmapDrawable> glideBitmapDrawableObservable = Observable.create(new GlideThumnailAfterRequestOnSubscribe(context, request, thumbnailRequest, imageView, errorId))
                .subscribeOn(AndroidSchedulers.mainThread());

        return glideBitmapDrawableObservable;
    }


}