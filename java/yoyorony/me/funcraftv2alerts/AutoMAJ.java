package yoyorony.me.funcraftv2alerts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;

import org.joda.time.DateTime;
import org.joda.time.Hours;

public class AutoMAJ extends Service {
    public static boolean reponsed = false;
    /**
     * TODO test automaj :
     * - si maj -> message a ouverture activity            A TEST -> OK
     * - si maj -> notif                                   A TEST -> OK
     * - notif non spam (snooze = forever)                 A TEST -> OK
     * - 1 verif 1 jour                                    A TEST
     * - stoper les verifs si maj                          A TEST -> OK
     */

    private Thread t;

    public static boolean isTimeToCheck() {
        DateTime lastCheck = null;
        if (FunApp.preferences.getInt("lastMAJYear", -1) != -1) {
            lastCheck = new DateTime(
                    FunApp.preferences.getInt("lastMAJYear", 0),
                    FunApp.preferences.getInt("lastMAJMonth", 0),
                    FunApp.preferences.getInt("lastMAJDay", 0),
                    FunApp.preferences.getInt("lastMAJHour", 0),
                    0);
        }

        if (lastCheck != null) {
            int hours = Hours.hoursBetween(lastCheck.toLocalDate(), (new DateTime()).toLocalDate()).getHours();
            return hours >= 24;
        }
        FunApp.preferenceseditor.putInt("lastMAJYear", (new DateTime()).getYear());
        FunApp.preferenceseditor.putInt("lastMAJMonth", (new DateTime()).getMonthOfYear());
        FunApp.preferenceseditor.putInt("lastMAJDay", (new DateTime()).getDayOfMonth());
        FunApp.preferenceseditor.putInt("lastMAJHour", (new DateTime()).getHourOfDay());
        FunApp.preferenceseditor.commit();
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (FunApp.preferences.getBoolean("automaj", true)) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(3600000);
                        } catch (InterruptedException ignored) {
                            break;
                        } //1 vérif/heure
                        if (Thread.interrupted()) {
                            break;
                        }
                        if (isTimeToCheck() && !FunApp.majdispo) {
                            Connexion.checkVersionSilence();
                            while (!reponsed) {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException ignored) {
                                    break;
                                }
                            }

                            if (FunApp.majdispo) {
                                Intent notificationIntent = new Intent(getBaseContext(), NotifsMAJIntentCall.class);
                                PendingIntent contentIntent = PendingIntent.getService(getBaseContext(), 0, notificationIntent, 0);
                                Notification.Builder notifbuilder = new Notification.Builder(getBaseContext())
                                        .setContentTitle("Mise à jour de Funcraft disponible")
                                        .setContentText("Cliquez sur la notification pour la télécharger")
                                        .setAutoCancel(true)
                                        .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher))
                                        .setSmallIcon(R.drawable.ic_stat_name)
                                        .setTicker("Mise à jour de Funcraft disponible")
                                        .setContentIntent(contentIntent);

                                Notification notif = notifbuilder.build();
                                NotificationManager notifmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notifmanager.notify(2102, notif);
                            }
                        }
                    }
                }
            });
            t.start();
            return START_STICKY;
        } else {
            stopSelf();
            return START_NOT_STICKY;
        }
    }

    @Override
    public Binder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            t.interrupt();
        } catch (NullPointerException ignored) {
        }
    }
}


class NotifsMAJIntentCall extends Service {
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