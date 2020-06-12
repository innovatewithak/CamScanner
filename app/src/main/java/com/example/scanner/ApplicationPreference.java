package com.example.scanner;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreference {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * There are two way to create and access the SharedPreference.
     *
     * 1. getSharedPreferences()
     *
     * 2. getPreferences()
     *
     * First one is used when we want to read or write the multiple
     * SharedPreference File, which are identified by a name(Unique String value)
     * for our SharedPreference the unique identification name is "app_name"
     *
     * Second one is used for a particular Activity. here it calls the
     * default SharedPreference and get the value with respect to that
     * Activity we don't have to specify the name.
     * */
    public ApplicationPreference(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(
                this.context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    /**
     * Difference between editor.commit(); & editor.apply();
     *
     * editor.commit(); will run Synchronously and writes the data
     * this may block the UI components.
     *
     * editor.apply(); will run Asynchronously ( run in Background )
     * this will not block any UI components.
     *
     * NOTE : use editor.apply(); for best for building best user friendly app.
     *
     * Below method will writs the data to SharedPreference
     * */
    public void setData(String key, String value){
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Below method will return the value from SharedPreference
     * based on the key.
     *
     * If there is no data returned from SharedPreference then we
     * need to specify the default value for our verification.
     *
     * in below method default value is @null.
     * */
    public String getData(String key){
        return sharedPreferences.getString(key, "");
    }

    /**
     * used to set int Data
     * */
    public void setIntData(String key, int value){
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getIntData(String key){
        return sharedPreferences.getInt(key, -1);
    }

    /**
     * used to set boolean data
     * */
    public void setBooleanData(String key, boolean value){
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanData(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Below method will clear all the shared preference value
     * which are associated with the 'Name' of the SharedPreference.
     * */
    public void clearPreference(){
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}