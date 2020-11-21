package com.abc.fyp_app_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class InstructionActivity extends AppCompatActivity {

    Intent getinstruc;
    String waystep;
    String[][] waysteparray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        getinstruc = getIntent();
        waystep = getinstruc.getExtras().getString("instruc");
        Log.i("zvalue",waystep);
        waysteparray = convertdata(waystep);
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