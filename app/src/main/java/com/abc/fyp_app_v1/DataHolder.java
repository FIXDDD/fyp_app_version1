package com.abc.fyp_app_v1;

import java.util.HashMap;

public class DataHolder {
    private HashMap<String, String[]> beacon_place= new HashMap<String, String[]>();
    private String[] roomArray = {"r1","r2"};
    private String[][] road;

    public DataHolder()
    {
        //put value to rooms Hashmap
        this.beacon_place.put("OufQT4", new String[]{"r1", "3"});
        this.beacon_place.put("OugKl0", new String[]{"c1", "3"});
        this.beacon_place.put("OuJ2KF", new String[]{"r2", "3"});
    }
    public HashMap<String, String[]> getbeacon_place() {
        return beacon_place;
    }
    public String[] getroomArray(){
        return roomArray;
    }
    public String[][] getroad(){
        return road;
    }
    public void setroad(String[][] x){
        this.road = x;
    }
}
