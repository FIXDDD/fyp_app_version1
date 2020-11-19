package com.abc.fyp_app_v1;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.Arrays;

public class JavaScriptInterface {
    Context mContext;

    JavaScriptInterface(Context c){
        mContext = c;
    }

    @JavascriptInterface
    public String[][] transmit(String d){
        Log.i("restest", d );
        return convertdata(d);
    }

    // for converting web recieve string to array format
    public String[][] convertdata(String data) {
        String[] recievedata = data.split(",");
        String[][] formatedata = new String[recievedata.length/3][3];
        int od = 0;
        int td = 0;
        for(int i = 0; i<recievedata.length; i++) {
            formatedata[od][td] = recievedata[i];
            td = td + 1;
            if(td ==3) {
                td = 0;
                od = od +1;
            }
        }
        //Log.i("formatdata", Arrays.deepToString(dataa.getroad()));
        Log.i("formatdata", Arrays.deepToString(formatedata));
        return formatedata;
    }

}
