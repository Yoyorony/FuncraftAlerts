package yoyorony.me.funcraftv2alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FunApp.preferences.getBoolean("activnotif", true)) {
            context.startService(new Intent(context, Notifs.class));
        }
    }
}