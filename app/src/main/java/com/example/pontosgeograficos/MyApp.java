package com.example.pontosgeograficos;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    public void onCreate() {
        super.onCreate();
        MyApp.context = this.getApplicationContext();
    }
}
