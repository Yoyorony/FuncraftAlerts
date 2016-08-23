package yoyorony.me.funcraftv2alerts;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;

import org.joda.time.DateTime;
import org.joda.time.Hours;

public class AutoMAJ extends IntentService {

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

    public AutoMAJ(){
        super("AutoMAJ");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (FunApp.preferences.getBoolean("automaj", true)) {
            if (isTimeToCheck() && !FunApp.majdispo) {
                Connexion.checkVersionSilence();

                if (FunApp.majdispo) {
                    Intent notificationIntent = new Intent(getBaseContext(), AutoMAJNotifsIntentCall.class);
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
        setAlarm(getBaseContext());
    }

    public static void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AutoMAJ.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        long frequency = 3600000; //1 check/heure
        am.set(AlarmManager.RTC_WAKEUP, frequency, pi);
    }
}