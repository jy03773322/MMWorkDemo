package main.mmwork.com.mmworklib.bindingcollectionadapter.factories;

import android.widget.AdapterView;

import main.mmwork.com.mmworklib.bindingcollectionadapter.BindingListViewAdapter;
import main.mmwork.com.mmworklib.bindingcollectionadapter.ItemViewArg;


public interface BindingAdapterViewFactory {

    public <T> BindingListViewAdapter<T> create(AdapterView adapterView, ItemViewArg<T> arg);

    public BindingAdapterViewFactory DEFAULT = new BindingAdapterViewFactory() {
        @Override
        public <T> BindingListViewAdapter<T> create(AdapterView adapterView, ItemViewArg<T> arg) {
            return new BindingListViewAdapter<>(arg);
        }
    };

}
