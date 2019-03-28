package com.example.a60570.weatherforecast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast intent: " + intent.getAction());
        SharedPreferences pref=context.getSharedPreferences("file",0);
        boolean isOn = pref.getBoolean("notification",true);
        PollService.setServiceAlarm(context, isOn);
    }
}

