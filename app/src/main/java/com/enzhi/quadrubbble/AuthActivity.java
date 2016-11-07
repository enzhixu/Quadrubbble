package com.enzhi.quadrubbble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class AuthActivity extends AppCompatActivity {
    public static final String KEY_URL = "url";
    public static final String KEY_CODE = "code";

    Toolbar toolbar;
    WebView webView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Log into Dribbble");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith(Auth.REDIRECT_URI)){
                    Uri uri = Uri.parse(url);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(KEY_CODE, uri.getQueryParameter(KEY_CODE)); //authCode
                    setResult(RESULT_OK, resultIntent);         //在這為什麼可以 setResult???
                    finish();               //什麼意思？？？
                }
                //return true;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        //getIntent().getStringExtra(KEY_URL);
        String url = getIntent().getStringExtra(KEY_URL);
        webView.loadUrl(url);
    }
}
