package main.mmwork.com.mmworklib.utils;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;


/**
 * Created by zhai on 16/2/22.
 */
public class ViewUtils {

    public static boolean setViewtLayoutWithHeight(View view, int wpx, int hpx) {
        float widthflod = (float) MMDisplayUtils.getScreenWidth() / wpx;
        int height = (int) (hpx * widthflod);
        ViewGroup.LayoutParams llp = view.getLayoutParams();
        if (llp.width == MMDisplayUtils.getScreenWidth() && llp.height == height) {
            return true;
        }
        llp.width = MMDisplayUtils.getScreenWidth();
        llp.height = height;
//        view.setLayoutParams(llp);
        Log.v("ViewUtils", "setViewtWidth");
        return false;
    }

    public static void setEditChangedBtnStatus(EditText editText, final View view) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String curString = String.valueOf(s);
                if (!TextUtils.isEmpty(curString)) {
                    view.setEnabled(true);
                } else {
                    view.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 设置margin
     *
     * @param view
     * @param tpx
     * @param bpx
     * @param lpx
     * @param rpx
     */
    @BindingAdapter({"bind:setViewMarginTop", "bind:setViewMarginBottom", "bind:setViewMarginLeft", "bind:setViewMarginRight"})
    public static void setViewtLayoutParams(View view, int tpx, int bpx, int lpx, int rpx) {
        RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) view.getLayoutParams();

        if (llp.leftMargin == tpx && llp.rightMargin == rpx
                && llp.topMargin == tpx && llp.bottomMargin == bpx) {
            return;
        }
/*        llp.setMargins(DensityUtility.dip2px(Global.getContext(), lpx),
                DensityUtility.dip2px(Global.getContext(), tpx), DensityUtility.dip2px(Global.getContext(), rpx),
                DensityUtility.dip2px(Global.getContext(), bpx));*/
        llp.setMargins(lpx, tpx, rpx, bpx);
        view.setLayoutParams(llp);
    }
}
