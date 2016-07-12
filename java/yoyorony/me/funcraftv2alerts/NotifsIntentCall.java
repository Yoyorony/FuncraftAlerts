package yoyorony.me.funcraftv2alerts;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;

public class NotifsIntentCall extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://community.funcraft.net/"));
        browserintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserintent);
        FunApp.timerStopRecherche = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {
                }
                FunApp.timerStopRecherche = false;
            }
        }).start();
        FunApp.alerts[0] = 0;
        FunApp.alerts[0] = 0;
        MainActivity.currentnotifcontent[0] = 0;
        MainActivity.currentnotifcontent[1] = 0;
        FunApp.preferenceseditor.putBoolean("notified", false);
        FunApp.preferenceseditor.commit();

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
