package com.medulance.driver.Fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sahil on 16/9/16.
 */
public abstract class BaseFragment extends Fragment {

    private View mFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(initializeLayoutId(), null);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getFragmentActivity();
        initializeViews(savedInstanceState);

    }

    /**
     * Get activity it is attached to
     */
    protected abstract Activity getFragmentActivity();

    /**
     * Initialize fragment views.
     */
    protected abstract void initializeViews(Bundle savedInstanceState);

    /**
     * @return returns layout id of the fragment.
     */
    protected abstract int initializeLayoutId();

}
