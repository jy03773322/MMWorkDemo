package main.mmwork.com.mmworklib.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import main.mmwork.com.mmworklib.utils.FileUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhai on 15/4/2.
 */
public class ImageWorker {
    public static final String TAG = ImageWorker.class.getName();

    public static void imageLoaderFitCenter(Context context, ImageView view, String thumnailUrl, String url) {
        DrawableRequestBuilder thumbNailRequest = Glide.with(context)
                .load(thumnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(url)
                .fitCenter()
                .thumbnail(thumbNailRequest)
                .dontAnimate()
                .into(view);
    }

    /**
     * 必须将Listener设置给Thumbnail的Builder，如果设置给主Builder，会造成大图图能正确替换
     *
     * @param context
     * @param view
     * @param thumnailUrl
     * @param url
     * @param listener
     */
    public static void imageLoaderFitCenter(Context context, ImageView view, String thumnailUrl, String url, RequestListener listener) {
        DrawableRequestBuilder thumbNailRequest = Glide.with(context)
                .load(thumnailUrl)
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(url)
                .fitCenter()
                .thumbnail(thumbNailRequest)
                .dontAnimate()
                .into(view);
    }

    /**
     * 常用于Detail页
     *
     * @param context
     * @param view
     * @param url
     * @param listener
     */
    public static void imageLoaderFitCenter(Context context, ImageView view, String url, RequestListener listener) {
        Glide.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .listener(listener)
                .into(view);
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            RequestManager request = Glide.with(context);
            request.load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(view);
        }
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, byte[] bytes) {
        if (null != bytes && 0 < bytes.length) {
            RequestManager request = Glide.with(context);
            request.load(bytes)
                    .fitCenter()
                    .into(view);
        }
    }

    public static void imageLoaderOnlyDownload(Context context, String url, RequestListener<Object, GlideDrawable> listener) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .listener(listener)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    public static void imageLoaderOnlyDownload(Context context, String url) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .preload();
    }

    public static void imageLoaderFitCenter(Context context, ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    public static void imageLoader(Context context, ImageView view, String url) {
        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        }
    }

    public static void imageLoader(Context context, ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }


    public static void imageWrapLoader(Context context, ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(view);
        }
    }

    public static void imageLoader(Context context, ImageView view, String url, String localUrl) {
        if (!TextUtils.isEmpty(url)) {
            ImageLoaderLoacalCacheEntity entity = new ImageLoaderLoacalCacheEntity(view, url, localUrl);
            getUrl(entity)
                    .subscribe(new Action1<ImageLoaderLoacalCacheEntity>() {
                        @Override
                        public void call(ImageLoaderLoacalCacheEntity entity) {
                            Glide.with(entity.imageView.getContext())
                                    .load(entity.url)
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(entity.imageView);
                        }
                    });
        }
    }


    /**
     * RXjava 实体类
     */
    private static class ImageLoaderLoacalCacheEntity {
        private ImageView imageView;
        private String url;
        private String localurl;


        public ImageLoaderLoacalCacheEntity(ImageView imageView, String url, String localurl) {
            this.imageView = imageView;
            this.url = url;
            this.localurl = localurl;
        }
    }

    public static Observable<ImageLoaderLoacalCacheEntity> getUrl(ImageLoaderLoacalCacheEntity entity) {
        return Observable.just(entity)
                .subscribeOn(Schedulers.computation())
                .doOnNext(new Action1<ImageLoaderLoacalCacheEntity>() {
                    @Override
                    public void call(ImageLoaderLoacalCacheEntity entity) {
                        if (!TextUtils.isEmpty(entity.localurl)) {
                            boolean isExists = FileUtils.exists(entity.localurl);
                            if (isExists) {
                                entity.url = entity.localurl;
                            }
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void imageLoaderCircle(final Context context, ImageView view, String url) {

        if (!TextUtils.isEmpty(url) && !isDestroyed(context)) {
            try {
                Glide.with(context)
                        .load(url)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(view);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "imageLoaderCircle: " + e.getMessage());
            }
        }
    }

    public static void imageLoader(Context context, ImageView view, int id) {
        Glide.with(context)
                .load(id)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(view);
    }

    public static void imageLoader(Context context, ImageView view, int placeholder, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .placeholder(placeholder)
                    .into(view);
        }
    }

    /**
     * 获取Bitmap回调
     *
     * @param context
     * @param url
     * @param simpleTarget
     */
    public static void imageLoaderBitmap(final Context context, String url, final SimpleTarget<Bitmap> simpleTarget) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(simpleTarget);
        }
    }

    /**
     * 获取圆角的drawable
     *
     * @param context
     * @param url
     * @param simpleTarget
     * @param pixels       圆角度
     */

    public static void imageLoaderRoundCorner(final Context context, String url, final SimpleTarget<GlideDrawable> simpleTarget, final float pixels) {
        if (!TextUtils.isEmpty(url) && !isDestroyed(context)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation(context, (int) pixels, 0))
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            simpleTarget.onResourceReady(resource, glideAnimation);
                        }
                    });

        }
    }

    public static void imageLoaderBlur(final Context context, final ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(context, 20))
                    .into(view);

        }
    }

    public static void imageLoaderRadius(final Context context, final ImageView view, String url, int radius) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .into(view);

        }
    }

    /**
     * 获得高斯模糊stringDrawableTypeRequest
     *
     * @param context
     * @param url
     * @return
     */
    public static DrawableTypeRequest<String> buildBlurBitmapRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(context, 20, 6));
            return stringDrawableTypeRequest;
        }

        return null;
    }

    public static DrawableTypeRequest<String> buildRoundedImageRequest(final Context context, String url, int radius) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .bitmapTransform(new RoundedCornersTransformation(context, radius, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            return stringDrawableTypeRequest;
        }
        return null;
    }

    public static DrawableTypeRequest<String> buildImageRequest(final Context context, String url, Transformation<Bitmap>... bitmapTransformations) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .bitmapTransform(bitmapTransformations)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            return stringDrawableTypeRequest;
        }
        return null;
    }

    public static DrawableTypeRequest<String> buildFitCenterImageRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();
            return stringDrawableTypeRequest;
        }
        return null;
    }

    public static DrawableTypeRequest<Integer> buildFitCenterImageRequest(final Context context, int url) {
        if (0 != url) {
            DrawableTypeRequest<Integer> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();
            return stringDrawableTypeRequest;
        }
        return null;
    }

    /**
     * 生成缩略图请求
     *
     * @param context
     * @param thumnailUrl
     * @return
     */
    public static DrawableTypeRequest<String> buildThumnaiRequest(final Context context, String thumnailUrl) {
        try {
            if (!TextUtils.isEmpty(thumnailUrl)) {
                DrawableTypeRequest<String> load = Glide.with(context).load(thumnailUrl);
                load.diskCacheStrategy(DiskCacheStrategy.ALL);
                return load;
            }
        } catch (Exception e) {
            Log.e(TAG, "buildThumnaiFlitCenterImageRequest: " + e.getMessage() + "/url=" + thumnailUrl);
        }
        return null;
    }

    public static DrawableTypeRequest<Integer> buildThumnaiFitCenterImageRequest(final Context context, int url) {
        if (0 != url) {
            DrawableTypeRequest<Integer> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();
            return stringDrawableTypeRequest;
        }
        return null;
    }


    public static DrawableTypeRequest<String> buildCenterCropImageRequest(final Context context, String url) {
        if (!TextUtils.isEmpty(url)) {
            DrawableTypeRequest<String> stringDrawableTypeRequest = Glide.with(context).load(url);
            stringDrawableTypeRequest
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop();
            return stringDrawableTypeRequest;
        }
        return null;
    }

    private static boolean isDestroyed(Context context) {
        if (context instanceof Activity) {
            if (((Activity) context).isDestroyed()) {
                return true;
            }
        }
        return false;
    }

}
