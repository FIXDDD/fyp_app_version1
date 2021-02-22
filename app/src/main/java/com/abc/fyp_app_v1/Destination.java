package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kontakt.sdk.android.common.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Destination extends AppCompatActivity {

    // call dataHolder class
    //DataHolder2 data = new DataHolder2();

    //The list of room in the area
    //String[] roomArray = {"r1","r2","r3","r4","r5","r6"};
    //String[] roomArray = data.getroomArray();
    //List<String> list = new ArrayList<String>(Arrays.asList(roomArray));

    // static HashMap to store which beacon is which area
    //public HashMap<String, String[]> beacon_place= data.getbeacon_place();

    //init database
    DatabaseHelper myDB;

    //array and list to store db data
    String[] roomArray;
    List<String> list;

    //view variable
    ListView roomlist;

    //intent variable
    Intent main_message;
    Intent navigate_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        this.setTitle("Please select your destination bellow");

        //setup database
        myDB = new DatabaseHelper(this);

        //setup array and list to store db data
        roomArray = roomslist(myDB);
        list = new ArrayList<String>(Arrays.asList(roomArray));

        //set intent
        navigate_message = new Intent(Destination.this,Navigate.class);
        main_message = getIntent();

        //final String x = beacon_place.get(main_message.getExtras().getString("NEAR_BEACON"))[0];
        final String x = myDB.findLocation(main_message.getExtras().getString("NEAR_BEACON"));
        Log.i("xvalue",x);
        list.remove(x);

        //set listview content
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.destination_listview, list);


        //get view objects
        roomlist = (ListView) findViewById(R.id.room_list);

        // set view item onclick listener
        roomlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get clicked item string
                String itemValue = (String)roomlist.getItemAtPosition(i);

                //send message to next activity
                if(main_message.getExtras().getString("NEAR_BEACON")!=null){
                    navigate_message.putExtra("STARTEND",x + "," +itemValue);
                    Log.i("NAV_MESSAGE",x + "," +itemValue);
                    navigate_message.putExtra("OAD",getIntent().getExtras().getBoolean("OAD"));
                    //start next activity
                    startActivity(navigate_message);
                    finish();
                }
            }
        });

        ListView listView = (ListView) findViewById(R.id.room_list);
        listView.setAdapter(adapter);
    }

    // Get list of Locations in database and remove all c beacons
    public String[] roomslist(DatabaseHelper myDB){
        Cursor data = myDB.showRooms();
        List<String> list = new ArrayList<String>();
        while(data.moveToNext()){
            list.add(data.getString(0));
        }
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).charAt(0) == 'c'){
                list.remove(list.get(i));
            }
        }
        String[] result= new String[list.size()];
        Log.i("test",Arrays.toString(list.toArray(result)));
        return list.toArray(result);
    }


}