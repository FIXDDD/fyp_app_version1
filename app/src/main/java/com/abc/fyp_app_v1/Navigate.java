package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Navigate extends AppCompatActivity {

    Intent pass_message;
    String[] startend;
    int loadcount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        pass_message = getIntent();
        startend = pass_message.getExtras().getString("STARTEND").split(",");

        // IF there is connection
        //  find Web view on main activity
        final WebView simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        simpleWebView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
        WebSettings webSettings = simpleWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        simpleWebView.setWebViewClient(new WebViewClient());
        simpleWebView.setWebChromeClient(new WebChromeClient());
        // specify the url of the web page in loadUrl function
        simpleWebView.loadUrl("http://192.168.1.22/fyp_test_web/");

        simpleWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                //String script="Javascript:Android.receive(navigate('"+startend[0]+"','"+startend[1]+"'))";
                String script="Javascript:navigate('"+startend[0]+"','"+startend[1]+"')";
                Log.i("SCRIPT",script);
                simpleWebView.loadUrl(script);
            }
        });
    }
}