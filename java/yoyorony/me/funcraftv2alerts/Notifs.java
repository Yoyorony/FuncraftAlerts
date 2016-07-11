package yoyorony.me.funcraftv2alerts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class Notifs extends Service {
    public static boolean needRefresh = false;
    public static NotificationManager notifmanager;
    private Thread t;

    public static long[] getVibrationPatern() {
        long[] patern;
        if (FunApp.preferences.getBoolean("customvibreur", false)) {
            patern = new long[6];
            patern[0] = 0;
            patern[1] = (FunApp.preferences.getInt("customvibreur1", 0) == 0 ? 0 : (FunApp.preferences.getInt("customvibreur1", 0) == 1 ? 500 : (FunApp.preferences.getInt("customvibreur1", 0) == 2 ? 200 : 100)));
            patern[2] = patern[1];
            patern[3] = (FunApp.preferences.getInt("customvibreur2", 0) == 0 ? 0 : (FunApp.preferences.getInt("customvibreur2", 0) == 1 ? 500 : (FunApp.preferences.getInt("customvibreur2", 0) == 2 ? 200 : 100)));
            patern[4] = patern[3];
            patern[5] = (FunApp.preferences.getInt("customvibreur3", 0) == 0 ? 0 : (FunApp.preferences.getInt("customvibreur3", 0) == 1 ? 500 : (FunApp.preferences.getInt("customvibreur3", 0) == 2 ? 200 : 100)));
        } else {
            patern = new long[]{0, 200, 200, 500};
        }
        return patern;
    }

    public static int getColorLed() {
        int color;
        color = (FunApp.preferences.getInt("customcolorled", 0) == 0 ? Color.RED : (FunApp.preferences.getInt("customcolorled", 0) == 1 ? Color.MAGENTA : (FunApp.preferences.getInt("customcolorled", 0) == 2 ? Color.BLUE : (FunApp.preferences.getInt("customcolorled", 0) == 3 ? Color.YELLOW : (FunApp.preferences.getInt("customcolorled", 0) == 4 ? Color.GREEN : (FunApp.preferences.getInt("customcolorled", 0) == 5 ? Color.rgb(255, 165, 0) : Color.WHITE))))));
        return color;
    }

    private long freqtomsconvert(int freq) {
        switch (freq) {//0:1mn  1:2mn  2:5mn  3:10mn  4:30mn  5:1h  6:2h
            case 0:
                return 60000;
            case 1:
                return 120000;
            case 2:
                return 300000;
            case 3:
                return 600000;
            case 4:
                return 1800000;
            case 5:
                return 3600000;
            case 6:
                return 7200000;
        }
        return -1;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final long time = freqtomsconvert(FunApp.preferences.getInt("freq", 2));
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Thread.interrupted()) {
                        break;
                    }
                    try {
                        if (!FunApp.preferences.getBoolean("activnotif", true)) {
                            break;
                        }
                        if (FunApp.preferences.getString("name", "").equals("") || FunApp.preferences.getString("mdp", "").equals("")) {
                            break;
                        }

                        Connexion.refreshAlerts();
                        Thread.sleep(1000);
                        if (Thread.interrupted()) {
                            break;
                        }

                        if (FunApp.alerts[0] == 0 && FunApp.alerts[1] == 0) {
                            MainActivity.currentnotifcontent[0] = 0;
                            MainActivity.currentnotifcontent[1] = 0;
                        }

                        if (needRefresh) { //when want convo/alert state change -> update notif
                            MainActivity.currentnotifcontent[0] = 0;
                            MainActivity.currentnotifcontent[1] = 0;
                            needRefresh = false;
                        }

                        if ((FunApp.alerts[0] != 0 || FunApp.alerts[1] != 0) //alerts[0] -> convos   alerts[1] -> alertes
                                && (FunApp.preferences.getBoolean("alerts", true) || FunApp.preferences.getBoolean("convos", true))
                                && (FunApp.alerts[0] != MainActivity.currentnotifcontent[0] || FunApp.alerts[1] != MainActivity.currentnotifcontent[1])
                                && ((FunApp.alerts[0] != 0 && FunApp.preferences.getBoolean("convos", true)) || (FunApp.alerts[1] != 0 && FunApp.preferences.getBoolean("alerts", true)))
                                && !FunApp.timerStopRecherche) {

                            String s = "";
                            if (FunApp.preferences.getBoolean("alerts", true)) {
                                if (FunApp.alerts[1] > 1) {
                                    s += String.valueOf(FunApp.alerts[1]) + " alertes";
                                } else if (FunApp.alerts[1] == 1) {
                                    s += String.valueOf(FunApp.alerts[1]) + " alerte";
                                }
                            }
                            if (FunApp.preferences.getBoolean("alerts", true) && FunApp.preferences.getBoolean("convos", true)) {
                                if (FunApp.alerts[0] > 0 && FunApp.alerts[1] > 0) {
                                    s += " et ";
                                }
                            }
                            if (FunApp.preferences.getBoolean("convos", true)) {
                                if (FunApp.alerts[0] > 1) {
                                    s += String.valueOf(FunApp.alerts[0]) + " conversations non lues";
                                } else if (FunApp.alerts[0] == 1) {
                                    s += String.valueOf(FunApp.alerts[0]) + " conversation non lue";
                                }
                            }

                            Intent notificationIntent = new Intent(getBaseContext(), NotifsIntentCall.class);
                            PendingIntent contentIntent = PendingIntent.getService(getBaseContext(), 0, notificationIntent, 0);
                            Intent notificationKillIntent = new Intent(getBaseContext(), NotifsIntentKillCall.class);
                            PendingIntent contentKillIntent = PendingIntent.getService(getBaseContext(), 0, notificationKillIntent, 0);
                            Notification.Builder notifbuilder = new Notification.Builder(getBaseContext())
                                    .setContentTitle(s)
                                    .setContentText("Notifications du forum Funcraft")
                                    .setContentInfo(String.valueOf(FunApp.alerts[0] + FunApp.alerts[1]))
                                    .setAutoCancel(true)
                                    .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher))
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setTicker(s)
                                    //TODO anticrampe .setDeleteIntent(contentKillIntent)
                                    .setContentIntent(contentIntent);

                            boolean b = FunApp.preferences.getBoolean("notified", false);
                            if (FunApp.preferences.getBoolean("vibreur", true) && !b) {
                                notifbuilder.setVibrate(getVibrationPatern());
                            }
                            if (FunApp.preferences.getBoolean("sonnerie", false) && !b) {
                                notifbuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            }
                            if (FunApp.preferences.getBoolean("led", false) && !b) {
                                notifbuilder.setLights(getColorLed(), 100, 500);
                            }
                            Notification notif = notifbuilder.build();
                            notifmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            notifmanager.notify(2101, notif);
                            FunApp.preferenceseditor.putBoolean("notified", true);
                            FunApp.preferenceseditor.commit();

                            MainActivity.currentnotifcontent[0] = FunApp.alerts[0];
                            MainActivity.currentnotifcontent[1] = FunApp.alerts[1];

                            if (Thread.interrupted()) {
                                break;
                            }
                            Thread.sleep(time - 1000);
                        } else if ((FunApp.alerts[0] == 0 && FunApp.alerts[1] == 0)
                                || (FunApp.alerts[1] == 0 && !FunApp.preferences.getBoolean("convos", true))
                                || (FunApp.alerts[0] == 0 && !FunApp.preferences.getBoolean("alerts", true))) {
                            try {
                                notifmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                notifmanager.cancel(2101);
                                if (FunApp.alerts[1] == 0 && !FunApp.preferences.getBoolean("convos", true)) {
                                    MainActivity.currentnotifcontent[0] = 0;
                                }
                                if (FunApp.alerts[1] == 0 && !FunApp.preferences.getBoolean("convos", true)) {
                                    MainActivity.currentnotifcontent[0] = 0;
                                }
                                FunApp.preferenceseditor.putBoolean("notified", false);
                                FunApp.preferenceseditor.commit();
                            } catch (NullPointerException ignored) {
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        t.start();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.interrupt();
    }
}


class NotifsIntentCall extends Service {
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