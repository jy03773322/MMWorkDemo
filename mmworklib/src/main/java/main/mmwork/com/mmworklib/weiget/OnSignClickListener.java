package main.mmwork.com.mmworklib.weiget;

import android.view.View;

import main.mmwork.com.mmworklib.utils.ClickUtil;

/**
 * Created by zhai on 16/6/29.
 */

public abstract class OnSignClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (!ClickUtil.isFastDoubleClick()) {
            onClickListener(v);
        }
    }

    public abstract void onClickListener(View v);
}

