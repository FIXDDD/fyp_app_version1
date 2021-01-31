package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.ISecureProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    //kontakt setup
    private ProximityManager proximityManager;

    //Map to store detected beacon
    private Map<String,Double> value;
    //Store nearest beacon
    private Map.Entry<String, Double> min;

    // variable for view object
    Button locationbtn;

    // loading for scan nearby beacon
    // for loading scan beacon
    ProgressDialog scanBeaconProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Press the button bellow for the application to find your location in the building");


        //initialize Kontakt
        KontaktSDK.initialize("DkDxdmEmVCGZDobylzFHLzNiudPrNfOX");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());

        //get view object
        locationbtn = (Button)findViewById(R.id.location_btn);

        //set beacon variable
        value = new HashMap<String,Double>();
        min = null;

        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getlocation();
                scanBeaconProgressDialog = ProgressDialog.show(MainActivity.this,"Detecting your location"
                        ,"Please Wait...",true);
                view.postDelayed(new Runnable() {
                    public void run() {
                        proximityManager.stopScanning();
                        scanBeaconProgressDialog.dismiss();
                        if(min != null) {
                            String nearbeacon = min.getKey();
                            Log.i("finalmin", "Hash map ~ " + min.getKey() + " : " + min.getValue() + " ");
                            //start destination activity and pass min to next activity
                            Intent intent = new Intent(MainActivity.this,Destination.class);
                            intent.putExtra("NEAR_BEACON",nearbeacon);
                            intent.putExtra("OAD",getIntent().getExtras().getBoolean("OAD"));
                            startActivity(intent);
                        }
                        else{
                            buildAlertMessageNoBeacon();
                        }
                    }
                }, 3000);
            }

        });
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }


    //onclick function to scan beacon
    private void getlocation(){
        startScanning();
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    //for scan beacon
    private SecureProfileListener createSecureProfileListener() {
        return new SecureProfileListener() {
            @Override
            public void onProfileDiscovered(ISecureProfile profile) {
                Log.i("Sample", "IBeacon discovered: " + profile.getUniqueId() + " " + profile.getTxPower() + " " + Double.valueOf(profile.getRssi()) + " " + calculateAccuracy(-75,Double.valueOf(profile.getRssi())));
                value.put(profile.getUniqueId(),calculateAccuracy(-77,Double.valueOf(profile.getRssi())));
                for(Map.Entry<String,Double> entry: value.entrySet()){
                    if(min == null|| min.getValue() > entry.getValue()){
                        min = entry;
                        Log.i("min","Hash map ~ " + min.getKey() + " : " + min.getValue() + " ");
                    }
                }
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

    // ALTER MESSAGE for gps
    protected void buildAlertMessageNoBeacon() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Error: Cannot detect your location, please try again.")
                .setCancelable(false)
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        return;
                    }
                })
                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        finish();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}