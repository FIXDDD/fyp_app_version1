package com.abc.fyp_app_v1;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JavaScriptInterface {
    Context mContext;
    String data;

    JavaScriptInterface(Context c){
        mContext = c;
    }

    @JavascriptInterface
    public void receive(String d){

        this.data = d;
        Log.i("restest", data );
    }
}
