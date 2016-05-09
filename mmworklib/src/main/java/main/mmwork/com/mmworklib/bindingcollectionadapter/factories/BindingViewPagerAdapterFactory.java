package main.mmwork.com.mmworklib.bindingcollectionadapter.factories;

import android.support.v4.view.ViewPager;

import main.mmwork.com.mmworklib.bindingcollectionadapter.BindingViewPagerAdapter;
import main.mmwork.com.mmworklib.bindingcollectionadapter.ItemViewArg;


public interface BindingViewPagerAdapterFactory {
    <T> BindingViewPagerAdapter<T> create(ViewPager viewPager, ItemViewArg<T> arg);

    BindingViewPagerAdapterFactory DEFAULT = new BindingViewPagerAdapterFactory() {
        @Override
        public <T> BindingViewPagerAdapter<T> create(ViewPager viewPager, ItemViewArg<T> arg) {
            return new BindingViewPagerAdapter<>(arg);
        }
    };

}
