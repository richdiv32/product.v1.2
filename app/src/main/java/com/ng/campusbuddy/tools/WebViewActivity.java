package com.ng.campusbuddy.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.ng.campusbuddy.R;


public class WebViewActivity extends Activity {
    Snackbar snackbar;
    WebView webView ;
    String url;

    private ProgressDialog progressx;
    Context context;
    String x="x";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        progressx=new ProgressDialog(this);
        progressx.setMessage("Please wait... ");
        progressx.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .a);
        snackbar = Snackbar
                .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                        startActivity(intent);

                    }
                });
        if (!isNetworkAvailable()) {
            snackbar.show();
        }
        progressx.show();

        Intent intent=getIntent();
        String url = intent.getStringExtra("Url");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        webView = (WebView) findViewById(R.id.mainWebView);


        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(new MyWebViewClient());
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptEnabled(true);

        //webPage.getSettings().setPluginState(PluginState.ON);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath("/data/data/" + "crm.agile.agilecrm" + "/databases/");



        webView.setWebChromeClient(new WebChromeClient());


//        webView.loadUrl("http://www.campusbuddy.com.ng");
        webView.loadUrl(url);
    }




    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isNetworkAvailable()) {
                snackbar.show();
            }else {
                view.loadUrl(url);

            }


            return true;

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (!isNetworkAvailable()) {
                snackbar.show();


            }
        }

        public void onPageFinished(WebView view, String url) {
            progressx.cancel();


        }

    }
    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    private class MyWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (!isNetworkAvailable()) {
                snackbar.show();


            }else {
                WebViewActivity.this.setValue(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        }
    }

    public void setValue(int progress) {
        this.progressx.setProgress(progress);
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
