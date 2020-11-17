package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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
        simpleWebView.loadUrl("http://172.28.53.66/fyp_test/");

        /*
        simpleWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged( WebView view, int newProgress) {
                if(newProgress==100){
                    String script="Javascript:navigate('"+startend[0]+"','"+startend[1]+"')";
                    Log.i("SCRIPT",script);
                    simpleWebView.loadUrl(script);
                }
            }
        });
        */
        simpleWebView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                //String script="Javascript:Android.receive(navigate('"+startend[0]+"','"+startend[1]+"'))";
                String script="Javascript:navigate('"+startend[0]+"','"+startend[1]+"')";
                Log.i("SCRIPT",script);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    simpleWebView.evaluateJavascript("navigate('"+startend[0]+"','"+startend[1]+"')",null);
                }
                else{
                    simpleWebView.loadUrl(script);
                }
            }
        });
    }
}