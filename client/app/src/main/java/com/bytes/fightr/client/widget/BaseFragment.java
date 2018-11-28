package com.bytes.fightr.client.widget;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by M. Silva on 1/3/16.
 */
public abstract class BaseFragment extends Fragment {

    @StringRes
    public abstract int getFragmentTitle();

    @LayoutRes
    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    public AppCompatActivity getActivityCompat() {
        return ((AppCompatActivity) getActivity());
    }
}
