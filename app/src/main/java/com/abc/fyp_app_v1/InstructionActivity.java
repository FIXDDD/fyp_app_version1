package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.SecureProfileListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.ISecureProfile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionActivity extends AppCompatActivity {

    //kontakt setup
    private ProximityManager proximityManager;

    //Dataholder
    DataHolder dataa = new DataHolder();

    //Map to store detected beacon
    public Map<String,Double> value2 =new HashMap<String,Double>();
    //Store nearest beacon
    public Map.Entry<String, Double> min2 = null;

    Intent getinstruc;
    String waystep;
    String[][] waysteparray;

    // get textview
    TextView direction;

    //step counter
    int step = 0;
    HashMap<String, String[]> beacon_placenow = dataa.getbeacon_place();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        //initialize Kontakt
        KontaktSDK.initialize("DkDxdmEmVCGZDobylzFHLzNiudPrNfOX");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());

        direction = (TextView)findViewById(R.id.Direction);

        getinstruc = getIntent();
        waystep = getinstruc.getExtras().getString("instruc");
        Log.i("zvalue",waystep);
        waysteparray = convertdata(waystep);
        direction.setText(Arrays.toString(waysteparray[step]));


    }

    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
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
                Log.i("Sample2", "IBeacon discovered: " + profile.getUniqueId() + " " + profile.getTxPower() + " " + Double.valueOf(profile.getRssi()) + " " + calculateAccuracy(-75,Double.valueOf(profile.getRssi())));
                value2.put(profile.getUniqueId(),calculateAccuracy(-75,calculateAccuracy(-75,Double.valueOf(profile.getRssi()))));
                for(Map.Entry<String,Double> entry: value2.entrySet()){
                    if(min2 == null|| min2.getValue() > entry.getValue()){
                        min2 = entry;
                        Log.i("min2","Hash map ~ " + min2.getKey() + " : " + min2.getValue() + " ");
                    }
                }
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> profiles) {
                for(ISecureProfile c: profiles) {
                    if (dataa.getbeacon_place().containsKey(c.getUniqueId())) {
                        Log.i("testupdate", c.getUniqueId() + ", " + dataa.getbeacon_place().get(c.getUniqueId())[0] + ", " + calculateAccuracy(-75, Double.valueOf(c.getRssi())));
                        value2.put(c.getUniqueId(), calculateAccuracy(-75, calculateAccuracy(-75, Double.valueOf(c.getRssi()))));
                        for (Map.Entry<String, Double> entry : value2.entrySet()) {
                            if (min2 == null || min2.getValue() > entry.getValue()) {
                                min2 = entry;
                                Log.i("now min2", "Hash map ~ " + min2.getKey() + " : " + min2.getValue() + " ");
                            }

                            //get text view form UI
                            direction = (TextView)findViewById(R.id.Direction);
                            getinstruc = getIntent();
                            waystep = getinstruc.getExtras().getString("instruc");
                            waysteparray = convertdata(waystep);
                            if (step<waysteparray.length-1) {
                                if (beacon_placenow.get(min2.getKey())[0].equals(waysteparray[step + 1][0])) {
                                    Log.i("stepnum", String.valueOf(step));
                                    step = step + 1;
                                    direction.setText(Arrays.toString(waysteparray[step]));
                                }
                            }
                            else if(step==waysteparray.length-1){
                                if (beacon_placenow.get(min2.getKey())[0].equals(waysteparray[step][1])){
                                    direction.setText(waysteparray[step][1]);
                                }
                            }
                        }
                    }
                }
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