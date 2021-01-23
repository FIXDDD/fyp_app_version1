package com.abc.fyp_app_v1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.concurrent.TimeUnit;

public class InstructionActivity extends AppCompatActivity implements SensorEventListener {


    //kontakt setup
    private ProximityManager proximityManager;

    //Dataholder
    DataHolder2 dataa = new DataHolder2();

    //Map to store detected beacon
    public Map<String,Double> value2 =new HashMap<String,Double>();
    //Store nearest beacon
    public Map.Entry<String, Double> min2 = null;

    // instruction variable
    Intent getinstruc;
    String waystep;
    String[][] waysteparray;

    // end button
    Button endbtn;
    // final text
    TextView textfinal;

    //step counter
    int step = 0;
    HashMap<String, String[]> beacon_placenow = dataa.getbeacon_place();

    // facing variable
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false;
    boolean haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    BluetoothAdapter bAdapter;

    MediaPlayer player;

    int OADcount=0;

    //Bluetooth part
// Receivers

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Receive", "onReceive: " + intent.getStringExtra("theMessage"));
            if (intent.getStringExtra("theMessage").equals("1") && OADcount == 0) {
                Toast.makeText(InstructionActivity.this, "Obstacle blocking the way within 1 meter!", Toast.LENGTH_SHORT).show();
                OADcount = OADcount + 1;
            };
            if (intent.getStringExtra("theMessage").equals("1") && OADcount > 0 && OADcount <5 ) {
                OADcount = OADcount + 1;
            };
            if (intent.getStringExtra("theMessage").equals("1") && OADcount >=5 ) {
                Toast.makeText(InstructionActivity.this, "Obstacle blocking the way within 1 meter!", Toast.LENGTH_SHORT).show();
                OADcount = 0;
            };
            if(intent.getStringExtra("theMessage").equals("0")){
                OADcount = 0;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        this.setTitle("Please follow instruction shown on screen bellow");

        if(getIntent().getExtras().getBoolean("OAD") == Boolean.TRUE) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));
        }

        //initialize Kontakt
        KontaktSDK.initialize("DkDxdmEmVCGZDobylzFHLzNiudPrNfOX");
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setSecureProfileListener(createSecureProfileListener());
        proximityManager.configuration().deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(2));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        endbtn = (Button)findViewById(R.id.donebtn);
        textfinal = (TextView) findViewById(R.id.final_text);

        // init user location
        getinstruc = getIntent();
        waystep = getinstruc.getExtras().getString("instruc");
        Log.i("zvalue",waystep);
        waysteparray = convertdata(waystep);

        //start find user facing
        compassstart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScanning();
        compassstart();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
        compassstop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        compassstop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compassstart();
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
                            getinstruc = getIntent();
                            waystep = getinstruc.getExtras().getString("instruc");
                            waysteparray = convertdata(waystep);
                            endbtn = (Button)findViewById(R.id.donebtn);
                            textfinal = (TextView) findViewById(R.id.final_text);

                            String[] now = waysteparray[step];
                            //variable to calculate faceing
                            int dirtocompass = 0;
                            String lasttext="";
                            String nextDes="";
                            if (step<waysteparray.length-1) {
                                nextDes = waysteparray[step][1];
                                if (beacon_placenow.get(min2.getKey())[0].equals(waysteparray[step + 1][0])) {
                                    if(player == null){
                                        player = MediaPlayer.create(getApplication().getApplicationContext() ,R.raw.log );
                                        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                stopPlayer();
                                            }
                                        });
                                    }
                                    player.start();
                                    Log.i("stepnum", String.valueOf(step));
                                    step = step + 1;
                                    nextDes = waysteparray[step][1];
                                }

                                dirtocompass = turndegree(waysteparray[step][2],mAzimuth);
                                // take the smallest turn
                                if(dirtocompass <-30 || dirtocompass >30) {
                                    if (dirtocompass < 180) {
                                        // Turn left : left degrees
                                        lasttext = "Turn Right " + String.valueOf(dirtocompass) + " degrees and walk straight for 3 meters to reach " + nextDes;
                                    } else {
                                        // Turn right : 360-left degrees
                                        lasttext = "Turn Left " + String.valueOf(360 - dirtocompass) + " degrees and walk straight for 3 meters to reach " + nextDes;
                                    }
                                    textfinal.setText(lasttext);
                                }
                                else{
                                    lasttext = "Walk straight for 3 meters to reach " + nextDes;
                                    textfinal.setText(lasttext);
                                }
                            }
                            else if(step==waysteparray.length-1){
                                nextDes = "Destination";
                                dirtocompass = turndegree(waysteparray[step][2],mAzimuth);
                                // take the smallest turn
                                if(dirtocompass <-30 || dirtocompass >30) {
                                    if (dirtocompass < 180) {
                                        // Turn left : left degrees
                                        lasttext = "Turn Right " + String.valueOf(dirtocompass) + " degrees and walk straight for 3 meters to reach " + nextDes;
                                    } else {
                                        // Turn right : 360-left degrees
                                        lasttext = "Turn Left " + String.valueOf(360 - dirtocompass) + " degrees and walk straight for 3 meters to reach " + nextDes;
                                    }
                                    textfinal.setText(lasttext);
                                }
                                else{
                                    lasttext = "Walk straight for 3 meters to reach " + nextDes;
                                    textfinal.setText(lasttext);
                                }
                                if (beacon_placenow.get(min2.getKey())[0].equals(waysteparray[step][1])){
                                    proximityManager.stopScanning();
                                    player = MediaPlayer.create(getApplication().getApplicationContext() ,R.raw.out );
                                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            stopPlayer();
                                        }
                                    });
                                    player.start();
                                    textfinal.setText("Done");
                                    endbtn.setVisibility(View.VISIBLE);
                                    endbtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            proximityManager.stopScanning();
                                            compassstop();
                                            LocalBroadcastManager.getInstance(InstructionActivity.this).unregisterReceiver(mReceiver);
                                            bAdapter = BluetoothAdapter.getDefaultAdapter();
                                            bAdapter.disable();
                                            Intent mStartActivity = new Intent(InstructionActivity.this, SettingActivity.class);
                                            startActivity(mStartActivity);
                                            Intent i = getBaseContext().getPackageManager()
                                                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                    });
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

    //sensor state function

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get sensor data
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
        mAzimuth = Math.round(mAzimuth);

        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // To start find user facing
    public void compassstart(){
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                // set listener
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            // set listener
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void compassstop() {
        if (haveSensor) {
            mSensorManager.unregisterListener(this, mRotationV);
        }
        else {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        }
    }

    // https://forum.arduino.cc/index.php?topic=94131.0
    public int turndegree(String face, int mAzimu ){
         int dir = 0;
         int tur = 0;


        if(face.equals("U")){
            dir = 360;
        }
        if(face.equals("D")){
            dir = 180;
        }
        if(face.equals("R")){
            dir = 90;
        }
        if(face.equals("L")){
            dir= 270;
        }

        if (dir < mAzimu){
            dir = dir + 360;
        }

        return tur = dir - mAzimu;

    }

    public void stopPlayer(){
        if (player !=null){
            player.release();
            player = null;
        }
    }

}