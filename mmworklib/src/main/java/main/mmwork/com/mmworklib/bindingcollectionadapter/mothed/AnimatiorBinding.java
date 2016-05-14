package main.mmwork.com.mmworklib.bindingcollectionadapter.mothed;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.view.View;


/**
 * Created by zhai on 16/5/3.
 */
public class AnimatiorBinding {

    public static final int DURATION = 300;

    @BindingAdapter({"bind:startAlphaAnimator"})
    public static void startAlphaAnimator(final View view, boolean isVisibll) {
        if (!isVisibll) {
            ObjectAnimator oa = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            oa.setDuration(DURATION);
            oa.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Log.i("Animation", "end");
                    view.setVisibility(View.GONE);
                }
            });
            oa.start();
        }else{
            view.setVisibility(View.VISIBLE);
        }
    }

    @BindingAdapter({"bind:startMountingAlphaAnimator"})
    public static void startMountingAlphaAnimator(final View view, boolean isVisibll) {
        if (isVisibll) {
            ObjectAnimator oa = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            oa.setDuration(400);
            oa.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setVisibility(View.VISIBLE);
                }

                public void onAnimationEnd(Animator animation) {
                    Log.i("Animation", "end");
                }
            });
            oa.start();
        }
    }
}
