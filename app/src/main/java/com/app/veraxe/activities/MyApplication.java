package com.app.veraxe.activities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


/**
 * Created by NITIN on 8/12/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // register with Active Android
       /// ActiveAndroid.initialize(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}