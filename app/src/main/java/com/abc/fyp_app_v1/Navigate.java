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
    static DataHolder dataa = new DataHolder();

    // beacoon scan manager
    private ProximityManager proximityManager;

    // message pass between activity
    Intent pass_message;
    // to seperate the start and end message from prievious activity
    String[] startend;

    HashMap<String, String[]> b_place = dataa.getbeacon_place();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);


        KontaktSDK.initialize("DkDxdmEmVCGZDobylzFHLzNiudPrNfOX");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());

        pass_message = getIntent();
        startend = pass_message.getExtras().getString("STARTEND").split(",");

        // IF there is connection
        //  find Web view on main activity
        final WebView simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        simpleWebView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
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


    // for scan ebeacon
    private SecureProfileListener createSecureProfileListener() {
        return new SecureProfileListener() {
            @Override
            public void onProfileDiscovered(ISecureProfile profile) {

            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
            }

            @Override
            public void onProfileLost(ISecureProfile profile) {

            }
        };
    }

    // Txpower is the signal power one meter away i  dbm and rssi is the signal receive now
    private double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
}