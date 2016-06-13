package main.mmwork.com.mmworklib.rxandroid.rxglid;

import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import main.mmwork.com.mmworklib.rxandroid.schedulers.AndroidSchedulers;
import rx.Observable;

/**
 * Created by zhai on 16/5/4.
 */
public class RxGlide {

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(DrawableTypeRequest<?> request) {
        if (null == request) {
            return Observable.empty();
        }
        return Observable.create(new GlideAfterRequestOnSubscribe(request))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<GlideBitmapDrawable> afterGlideRequestListener(DrawableTypeRequest<?> request, ImageView imageView) {
        if (null == request) {
            return Observable.empty();
        }
        return Observable.create(new GlideAfterRequestOnSubscribe(request, imageView))
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}