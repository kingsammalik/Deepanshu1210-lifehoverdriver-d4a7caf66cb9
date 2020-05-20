package com.medulance.driver.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.medulance.driver.App.Constants;
import com.medulance.driver.R;

/**
 * Created by sahil on 23/9/16.
 */
public class MiscFragment extends BaseFragment {

    private WebView webview;
    private ProgressBar pb_progress;
    private Activity mActivity;

    @Override
    protected Activity getFragmentActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        initializeLayouts();

        webview.getSettings().setJavaScriptEnabled(true);
        showUrl(getArguments().getInt(Constants.IntentParameters.WHICH, 1));
        webview.setWebViewClient(new MyBrowser());
    }

    private void showUrl(int which) {
        switch (which) {
            case 1:
                webview.loadUrl(Constants.WebViewUrls.ABOUT);
                break;
            default:
                webview.loadUrl(Constants.WebViewUrls.ABOUT);
                break;
        }
    }

    private void initializeLayouts() {
        webview = (WebView) getFragmentActivity().findViewById(R.id.webview);
        pb_progress = (ProgressBar) getFragmentActivity().findViewById(R.id.pb_progress);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb_progress.setVisibility(View.GONE);
        }
    }

    @Override
    protected int initializeLayoutId() {
        return R.layout.fragment_misc;
    }
}
