package fivemoreminutes.a5moreminutes.Settings;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import fivemoreminutes.a5moreminutes.Entity.App;

public class SharPreferences implements Serializable{
    private SharedPreferences mSettings;
    public boolean allLimit;
    public boolean notific;
    public boolean popNotific;
    public int limit_hour;
    public int limit_min;
    public Map<String,Map<String,App>> result;


    public SharPreferences(SharedPreferences  _mSettings) {
        this.mSettings = _mSettings;
    }

    public SharedPreferences getmSettings() {
        return mSettings;
    }

    public boolean isAllLimit() {
        return allLimit;
    }

    public boolean isNotific() {
        return notific;
    }

    public boolean isPopNotific() {
        return popNotific;
    }

    public int getLimit_hour() {
        return limit_hour;
    }

    public int getLimit_min() {
        return limit_min;
    }

    public Map<String,Map<String,App>> getResult() {
        return result;
    }

    public void saveMap(Intent intent, Map<String,Map<String,App>> map){
        SharedPreferences.Editor editor = mSettings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString("map", json);
        editor.commit();
        editor.apply();
    }

    public void loadMap(Intent intent){
        if (mSettings.contains("map")) {
            Gson gson = new Gson();
            String json = mSettings.getString("map", null);
            Type type = new TypeToken<Map<String,Map<String,App>>>() {}.getType();
            result = gson.fromJson(json, type);
        }
        intent.putExtra("map", (Serializable) result);
    }

    public void saveLimitSwitch(Intent intent, boolean allLimit){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("allLimit", allLimit);
        editor.apply();
    }
    public void saveLimit(Intent intent, int limit_hour,int limit_min){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("Hour", limit_hour);
        editor.putInt("Min", limit_min);
        editor.apply();
    }

    public void saveNotific(Intent intent, boolean notific){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("notific", notific);
        editor.apply();
    }

    public void savePopNotific(Intent intent, boolean popNotific){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("popNotific", popNotific);
        editor.apply();
    }

    public void loadLimitSwitch(Intent intent){
        if (mSettings.contains("allLimit")) {
            allLimit = mSettings.getBoolean("allLimit",false);
        }
        intent.putExtra("allLimit", allLimit);
    }

    public void loadLimit(Intent intent){

        if (mSettings.contains("Hour")) {
            limit_hour = mSettings.getInt("Hour",0);
        }
        if (mSettings.contains("Min")) {
            limit_min = mSettings.getInt("Min",0);
        }
        intent.putExtra("Hour", limit_hour);
        intent.putExtra("Min", limit_min);
    }

    public void loadNotific(Intent intent){

        if (mSettings.contains("popNotific")) {
            popNotific = mSettings.getBoolean("popNotific",false);
        }
        intent.putExtra("popNotific", popNotific);
    }

    public void loadPopNotific(Intent intent){

        if (mSettings.contains("notific")) {
            notific = mSettings.getBoolean("notific",false);
        }
        intent.putExtra("notific", notific);
    }

}
