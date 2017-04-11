package com.example.user.project;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class MyApplication extends Application {
    private static TcpClient mTcpClient = null;
    private static Context context;

    public static TcpClient getTcpClient() {
        return mTcpClient;
    }

    public static void setTcpClient(TcpClient TcpClient) {
        mTcpClient = TcpClient;
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext(){
        return context;
    }

    public static char getClientType(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPref.getString("ClientType","2").charAt(0);
    }
}
