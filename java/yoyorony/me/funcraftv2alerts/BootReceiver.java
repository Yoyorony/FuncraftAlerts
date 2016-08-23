package yoyorony.me.funcraftv2alerts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FunApp.preferences.getBoolean("activnotif", true)) {
            Notifs.setAlarm(context);
        }
        AutoMAJ.setAlarm(context);
    }
}