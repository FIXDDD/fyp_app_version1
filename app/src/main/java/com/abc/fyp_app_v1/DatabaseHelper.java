package com.abc.fyp_app_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.SQLClientInfoException;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Data.db";
    public static final String TABLE_NAME1 = "Beacon_data";
    public static final String T1_col1 = "LOCATIONS";
    public static final String T1_col2 = "BEACONS";
    public static final String TABLE_NAME2 = "Beacon_distance";
    public static final String T2_col1 = "STARTBEACON";
    public static final String T2_col2 = "ENDBEACON";
    public static final String T2_col3 = "DISTANCE";
    public static final String createTable1 = "CREATE TABLE " + TABLE_NAME1 + "(LOCATIONS TEXT,BEACONS TEXT," +
            "primary key (LOCATIONS));";
    public static final String createTable2 = "CREATE TABLE " + TABLE_NAME2 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            " STARTBEACON TEXT,ENDBEACON TEXT,DISTANCE INT, FOREIGN KEY(STARTBEACON) REFERENCES "+TABLE_NAME1+"(LOCATIONS));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable1);
        db.execSQL(createTable2);
        adddata(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP  TABLE IF EXISTS " + TABLE_NAME1);
        db.execSQL("DROP  TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    public void adddata(SQLiteDatabase db){

        //Data of T1
        ContentValues T1_r1 = new ContentValues();
        T1_r1.put(T1_col1,"r1");
        T1_r1.put(T1_col2,"OufQT4");
        db.insert(TABLE_NAME1,null,T1_r1);

        ContentValues T1_c1 = new ContentValues();
        T1_c1.put(T1_col1,"c1");
        T1_c1.put(T1_col2,"OugKl0");
        db.insert(TABLE_NAME1,null,T1_c1);

        ContentValues T1_r2 = new ContentValues();
        T1_r2.put(T1_col1,"r2");
        T1_r2.put(T1_col2,"OuJ2kF");
        db.insert(TABLE_NAME1,null,T1_r2);

        ContentValues T1_r3 = new ContentValues();
        T1_r3.put(T1_col1,"r3");
        T1_r3.put(T1_col2,"OuQ0Lp");
        db.insert(TABLE_NAME1,null,T1_r3);

        //Data of T2

        //r1 to other
        ContentValues T2_r1_c1 = new ContentValues();
        T2_r1_c1.put(T2_col1,"r1");
        T2_r1_c1.put(T2_col2,"c1");
        T2_r1_c1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r1_c1);

        ContentValues T2_r1_r2 = new ContentValues();
        T2_r1_r2.put(T2_col1,"r1");
        T2_r1_r2.put(T2_col2,"r2");
        T2_r1_r2.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r1_r2);

        ContentValues T2_r1_r3 = new ContentValues();
        T2_r1_r3.put(T2_col1,"r1");
        T2_r1_r3.put(T2_col2,"r3");
        T2_r1_r3.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r1_r3);

        //c1 to other
        ContentValues T2_c1_r1 = new ContentValues();
        T2_c1_r1.put(T2_col1,"c1");
        T2_c1_r1.put(T2_col2,"r1");
        T2_c1_r1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_c1_r1);

        ContentValues T2_c1_r2 = new ContentValues();
        T2_c1_r2.put(T2_col1,"c1");
        T2_c1_r2.put(T2_col2,"r2");
        T2_c1_r2.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_c1_r2);

        ContentValues T2_c1_r3 = new ContentValues();
        T2_c1_r3.put(T2_col1,"c1");
        T2_c1_r3.put(T2_col2,"r3");
        T2_c1_r3.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_c1_r3);

        //r2 to other
        ContentValues T2_r2_c1 = new ContentValues();
        T2_r2_c1.put(T2_col1,"r2");
        T2_r2_c1.put(T2_col2,"c1");
        T2_r2_c1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r2_c1);

        ContentValues T2_r2_r1 = new ContentValues();
        T2_r2_r1.put(T2_col1,"r2");
        T2_r2_r1.put(T2_col2,"r1");
        T2_r2_r1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r2_r1);

        ContentValues T2_r2_r3 = new ContentValues();
        T2_r2_r3.put(T2_col1,"r2");
        T2_r2_r3.put(T2_col2,"r3");
        T2_r2_r3.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r2_r3);

        //r3 to other
        ContentValues T2_r3_c1 = new ContentValues();
        T2_r3_c1.put(T2_col1,"r3");
        T2_r3_c1.put(T2_col2,"c1");
        T2_r3_c1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r3_c1);

        ContentValues T2_r3_r2 = new ContentValues();
        T2_r3_r2.put(T2_col1,"r3");
        T2_r3_r2.put(T2_col2,"r2");
        T2_r3_r2.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r3_r2);

        ContentValues T2_r3_r1 = new ContentValues();
        T2_r3_r1.put(T2_col1,"r3");
        T2_r3_r1.put(T2_col2,"r1");
        T2_r3_r1.put(T2_col3,3);
        db.insert(TABLE_NAME2,null,T2_r3_r1);
    }

    public Cursor showRooms(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+ T1_col1 +" FROM " + TABLE_NAME1, null);
        return data;
    }

    public String findBeacon(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+ T1_col2 +" FROM " + TABLE_NAME1 + " WHERE " + T1_col1 + " = " + "\"" + location + "\"", null);
        data.moveToNext();
        return data.getString(0);
    }

    public String findLocation(String beacon){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+ T1_col1 +" FROM " + TABLE_NAME1 + " WHERE " + T1_col2 + " = " + "\"" + beacon+ "\"", null);
        data.moveToNext();
        return data.getString(0);
    }

    public boolean containsBeacon(String beacon) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT COUNT(1) FROM " + TABLE_NAME1 + " WHERE " + T1_col2 + " = " + "\"" + beacon + "\"", null);
        data.moveToNext();
        if (data.getInt(0) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int distanceBetweenBeacon(String x,String y){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT "+ T2_col3 + " FROM " + TABLE_NAME2 + " WHERE " + T2_col1 + " = " + "\"" + x + "\" and "+ T2_col2 + " = "+ "\"" + y + "\"", null);
        data.moveToNext();
        return data.getInt(0);
    }
}
