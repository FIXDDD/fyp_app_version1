package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Navigate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // IF there is connection
        //  find Web view on main activity
        final WebView simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        WebSettings webSettings = simpleWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        simpleWebView.setWebViewClient(new WebViewClient());
        simpleWebView.setWebChromeClient(new WebChromeClient());
        // specify the url of the web page in loadUrl function
        simpleWebView.loadUrl("http://192.168.1.22:8000/");
    }
}