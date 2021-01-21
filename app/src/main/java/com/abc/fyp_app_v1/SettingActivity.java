package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.UUID;

public class SettingActivity extends AppCompatActivity {

    //Get UI button
    // variable for view object
    Button raspscanbtn;

    //bluetooth connection with raspberry
    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    String deviceID = "DC:A6:32:A2:19:BC";


    //For gps get permission
    private static final int REQUEST_LOCATION = 1;
    boolean gpson = true;

    // for loading scan beacon
    ProgressDialog scanProgressDialog;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("STATE OFF", "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("TURNING OFF", "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("STATE ON", "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("TURNING ON", "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("New device", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                Log.d("device info", "onReceive: " + device.getName() + ": " + device.getAddress());
                if (device.getAddress().equals(deviceID)){
                    mBTDevice = device;
                    Log.d("Found device", "Raspberry pi");
                }
            }
        }
    };

    BluetoothServerSocket mmServerSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.setTitle("Press the button bellow to connect the obstacle avoidance device.");

        //get view object
        raspscanbtn = (Button)findViewById(R.id.raspscan_btn);

        raspscanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectrasp(view);
            }
        });

        // get ACCESS_FINE_LOCATION granted
        if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (SettingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        // set bT Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Log.d("Not BT", "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d("Enabling BT", "enableDisableBT: enabling BT.");
            buildAlertMessageNoBT();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
            checkGPS();
        }
        else{
            checkGPS();
        }
    }

    //check gps alive
    private void checkGPS(){

        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestHighAccuracy);
        Task<LocationSettingsResponse> gpsResult =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        gpsResult.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            buildAlertMessageNoGps();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            gpson = false;
                            break;
                    }
                }
            }
        });
    }

    // ALTER MESSAGE for gps
    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    // ALTER MESSAGE for gps
    protected void buildAlertMessageNoBT() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your Bluetooth")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivity(enableBTIntent);
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

    private void connectrasp(View view){
        mBluetoothAdapter.startDiscovery();
        mBluetoothConnection = new BluetoothConnectionService(SettingActivity.this);
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        scanProgressDialog = ProgressDialog.show(SettingActivity.this,"Scanning nearby bluetooth device"
                ,"Please Wait...",true);
        view.postDelayed(new Runnable() {
            public void run() {
                mBluetoothAdapter.cancelDiscovery();
                scanProgressDialog.dismiss();
                if (mBTDevice != null){
                    buildAlertMessageConnectDevice();
                }else{
                    buildAlertMessageNoRasp();
                }
                }
            }
        , 5000);

    }

    // ALTER MESSAGE for gps
    protected void buildAlertMessageConnectDevice() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Object Avoidance device nearby, Would you like to connect to the device?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Log.i("Connect to raspberry","Connecting to raspberry");
                        mBluetoothConnection.startClient(mBTDevice,MY_UUID_INSECURE);
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        intent.putExtra("OAD",Boolean.TRUE);
                        startActivity(intent);
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

    // ALTER MESSAGE for gps
    protected void buildAlertMessageNoRasp() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Object Avoidance device not found, please try again")
                .setCancelable(false)
                .setPositiveButton("Scan again", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        return;
                    }
                })
                .setNegativeButton("Continue without it", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                        intent.putExtra("OAD",Boolean.FALSE);
                        startActivity(intent);

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public BluetoothConnectionService getmBluetoothConnection(){
        return mBluetoothConnection;
    }
}