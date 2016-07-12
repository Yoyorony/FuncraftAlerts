package yoyorony.me.funcraftv2alerts;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;

public class AutoMAJNotifsIntentCall extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://serveur24sur24.free.fr/FuncraftAlerts/FuncraftAlerts.apk"));
        browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserintent);

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public Binder onBind(Intent intent) {
        return null;
    }
}