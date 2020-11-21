package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.ISecureProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Navigate extends AppCompatActivity {

    // call dataHolder class
    DataHolder dataa = new DataHolder();

    //save gen road
    String[][] road;

    myjsinterface javascriptinterface = new myjsinterface();

    // beacoon scan manager
    private ProximityManager proximityManager;

    // message pass between Destionation and navigaton activity
    Intent pass_message;
    // message pass to instruction activity
    Intent instruction;

    // to seperate the start and end message from prievious activity
    String[] startend;

    HashMap<String, String[]> b_place = dataa.getbeacon_place();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        pass_message = getIntent();
        startend = pass_message.getExtras().getString("STARTEND").split(",");

        // IF there is connection
        //  find Web view on main activity
        final WebView simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        simpleWebView.addJavascriptInterface(javascriptinterface,"Android");
        WebSettings webSettings = simpleWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowContentAccess(true);
        simpleWebView.setWebViewClient(new WebViewClient());
        simpleWebView.setWebChromeClient(new WebChromeClient());
        // specify the url of the web page in loadUrl function
        simpleWebView.loadUrl("file:///android_asset/fyp_test_web/index.html");

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
                String script="Javascript:Android.transmit(transmit('"+startend[0]+"','"+startend[1]+"'))";
                Log.i("SCRIPT",script);
                simpleWebView.loadUrl(script);
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //simpleWebView.evaluateJavascript("transmit('"+startend[0]+"','"+startend[1]+"')",null)
                //}
                //else{
                //    simpleWebView.loadUrl(script);
                //}
            }
        });



    }

    class myjsinterface {
        @JavascriptInterface
        public void transmit(String d) {
            Log.i("restest", d);
            instruction = new Intent(Navigate.this,InstructionActivity.class);
            instruction.putExtra("instruc",d);
            startActivity(instruction);
        }
    }


}