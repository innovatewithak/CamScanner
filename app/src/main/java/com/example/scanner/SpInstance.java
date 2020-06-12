package com.example.scanner;

import android.content.Context;


public class SpInstance {

    public static ApplicationPreference sp_instance;

    //constructor
    private SpInstance() {
    }


    public static ApplicationPreference getInstance(Context context) {
        if (sp_instance == null) {
            sp_instance = new ApplicationPreference(context);
        }
        return sp_instance;
    }
}



