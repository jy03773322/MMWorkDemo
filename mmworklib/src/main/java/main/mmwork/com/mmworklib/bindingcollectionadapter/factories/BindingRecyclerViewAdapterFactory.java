package main.mmwork.com.mmworklib.bindingcollectionadapter.factories;


import android.support.v7.widget.RecyclerView;

import main.mmwork.com.mmworklib.bindingcollectionadapter.BindingRecyclerViewAdapter;
import main.mmwork.com.mmworklib.bindingcollectionadapter.ItemViewArg;


public interface BindingRecyclerViewAdapterFactory {
    <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg);

    BindingRecyclerViewAdapterFactory DEFAULT = new BindingRecyclerViewAdapterFactory() {
        @Override
        public <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg) {
            return new BindingRecyclerViewAdapter<>(arg);
        }
    };
}
