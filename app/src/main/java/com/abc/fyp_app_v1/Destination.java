package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Destination extends AppCompatActivity {

    //The list of room in the area
    // ! improve: My call server to receive this message
    String[] roomArray = {"Room1","Room2","Room3","Room4","Room5","Room6","Room7","Room8","Room9"};

    //view variable
    ListView roomlist;

    //intent variable
    Intent main_message;
    Intent navigate_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        //set listview content
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.destination_listview, roomArray);

        //set intent
        navigate_message = new Intent(Destination.this,Navigate.class);
        main_message = getIntent();

        //get view objects
        roomlist = (ListView) findViewById(R.id.room_list);

        // set view item onclick listener
        roomlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get clicked item string
                String itemValue = (String)roomlist.getItemAtPosition(i);

                //send message to next activity
                navigate_message.putExtra("STARTEND",main_message.getStringArrayExtra("NEAR_BEACON") + "," +itemValue);
                Log.i("NAV_MESSAGE","main_message.getStringArrayExtra(\"NEAR_BEACON\") + \",\" +itemValue");
                //start next activity
                startActivity(navigate_message);
            }
        });




        ListView listView = (ListView) findViewById(R.id.room_list);
        listView.setAdapter(adapter);
    }


}