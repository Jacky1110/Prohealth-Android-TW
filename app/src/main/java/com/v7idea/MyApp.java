package com.v7idea;

import android.app.Application;

import com.v7idea.healthkit.Air;

public class MyApp extends Application {
    private Air a;

    public void onCreate() {
        super.onCreate();
        a = new Air();
    }
    public Air getAir(){
        return a;
    }
}
