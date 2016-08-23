package com.mbv.pokket.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by arindamnath on 07/02/16.
 */
public class AppWebViewClient extends WebViewClient {

    private ProgressDialog progressDialog;

    public AppWebViewClient(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        progressDialog.show();
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressDialog.dismiss();
    }
}
