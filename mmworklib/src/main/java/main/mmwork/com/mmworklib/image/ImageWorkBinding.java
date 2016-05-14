package main.mmwork.com.mmworklib.image;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestListener;

import main.mmwork.com.mmworklib.utils.ViewUtils;
import yanzhi.xingyun.com.mmworklib.R;

/**
 * Created by zhai on 16/2/19.
 */
public class ImageWorkBinding {
    private static final String TAG = "ImageWorkBinding";

    @BindingAdapter({"bind:loadBytesFitCenterImage"})
    public static void loadBytesFitCenterImage(ImageView view, byte[] bytes) {
        ImageWorker.imageLoaderFitCenter(view.getContext(), view, bytes);
    }

    @BindingAdapter({"bind:loadFitCenterImage"})
    public static void loadFitCenterImage(ImageView view, String url) {
        ImageWorker.imageLoaderFitCenter(view.getContext(), view, url);
    }

    @BindingAdapter({"bind:imageLoaderFitCenter", "bind:thumbnail"})
    public static void loadFitCenterImage(ImageView view, String url, String thumbnailUrl) {
        ImageWorker.imageLoaderFitCenter(view.getContext(), view, thumbnailUrl, url);
    }

    @BindingAdapter({"bind:imageLoaderFitCenter", "bind:thumbnail", "imageCallback"})
    public static void loadFitCenterImage(ImageView view, String url, String thumbnailUrl, RequestListener listener) {
        ImageWorker.imageLoaderFitCenter(view.getContext(), view, thumbnailUrl, url, listener);
    }

    @BindingAdapter({"bind:imageLoaderFitCenter", "imageCallback"})
    public static void loadFitCenterImage(ImageView view, String url, RequestListener listener) {
        ImageWorker.imageLoaderFitCenter(view.getContext(), view, url, listener);
    }

    @BindingAdapter({"bind:loadImage", "loadImageLocal"})
    public static void loadImage(ImageView view, String url, String local) {
        if (TextUtils.isEmpty(local)) {
            ImageWorker.imageLoader(view.getContext(), view, url);
        } else {
            ImageWorker.imageLoader(view.getContext(), view, url, local);
        }
    }

    @BindingAdapter({"bind:loadImage"})
    public static void loadImage(ImageView view, String url) {
        ImageWorker.imageLoader(view.getContext(), view, url);
    }

    @BindingAdapter({"bind:loadWrapImage"})
    public static void loadWrapImage(ImageView view, String url) {
        ImageWorker.imageWrapLoader(view.getContext(), view, url);
    }

    @BindingAdapter({"bind:loadBlurImage"})
    public static void loadBlurImage(ImageView view, String url) {
        ImageWorker.imageLoaderBlur(view.getContext(), view, url);
    }

    @BindingAdapter({"bind:loadRadiusImage", "imageRadius"})
    public static void loadRadiusImage(ImageView view, String url, int radius) {
        ImageWorker.imageLoaderRadius(view.getContext(), view, url, radius);
    }

    @BindingAdapter({"bind:loadRadiusImage"})
    public static void loadRadiusImage(ImageView view, String url) {
        ImageWorker.imageLoaderRadius(view.getContext(), view, url, view.getContext().getResources().getDimensionPixelSize(R.dimen.radius_max));
    }

    @BindingAdapter({"bind:loadImage"})
    public static void loadImage(ImageView view, Bitmap bitmap) {
        ImageWorker.imageLoader(view.getContext(), view, bitmap);
    }

    @BindingAdapter({"bind:imageLoaderCircle"})
    public static void imageLoaderCircle(ImageView view, String url) {
        ImageWorker.imageLoaderCircle(view.getContext(), view, url);
    }

    @BindingAdapter({"bind:loadImage", "bind:measureWidthWithHeight", "bind:measureHeightWithHeight"})
    public static void imageLoaader(ImageView view, String url, int wpx, int hpx) {
        boolean isLayoutParams = ViewUtils.setViewtLayoutWithHeight(view, wpx, hpx);
        if (!isLayoutParams) {
            ImageWorker.imageLoader(view.getContext(), view, url);
            Log.d(TAG, "imageLoaader: bind:measureWidthWithHeight, bind:measureHeightWithHeight");
        }
    }

    @BindingAdapter({"bind:measureWidthWithHeightRatio"})
    public static void measureWidthWithHeightRatio(ImageView view, int wpx, int hpx) {
        ViewUtils.setViewtLayoutWithHeight(view, wpx, hpx);
    }


}
