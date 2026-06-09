package com.example.findinglogs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.findinglogs.service.WeatherStartupService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "Broadcast_Boot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: celular reiniciou, iniciando WeatherStartupService");

            Intent serviceIntent = new Intent(context, WeatherStartupService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}