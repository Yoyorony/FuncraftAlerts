package yoyorony.me.funcraftv2alerts;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;

class NotifsIntentKillCall extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO anti crampe

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public Binder onBind(Intent intent) {
        return null;
    }
}
