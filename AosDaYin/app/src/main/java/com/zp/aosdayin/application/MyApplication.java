package com.zp.aosdayin.application;

import android.app.Application;
import android.content.Context;


public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static MyApplication app;


    public synchronized static MyApplication getAppContext() {
        return app;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
