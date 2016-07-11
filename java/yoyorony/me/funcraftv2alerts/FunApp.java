package yoyorony.me.funcraftv2alerts;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FunApp extends Application {
    public static ConnectivityManager connectivityManager;
    public static SharedPreferences preferences;
    public static int[] alerts = new int[]{0, 0};
    public static SharedPreferences.Editor preferenceseditor;
    public static boolean timerStopRecherche = false;
    public static boolean majdispo;

    @Override
    public void onCreate() {
        majdispo = false;
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable ex) {
                ex.printStackTrace();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String version = "Unknown";
                try {
                    version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException ignored) {
                }
                String versionCode = "Unknown";
                try {
                    versionCode = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
                } catch (PackageManager.NameNotFoundException ignored) {
                }
                String s = "Brand : " + Build.BRAND;
                s += "\nDevice : " + Build.DEVICE;
                s += "\nModel : " + Build.MODEL;
                s += "\nSerial : " + Build.SERIAL;
                s += "\nAndroid version : " + Build.VERSION.RELEASE;
                s += "\nApp version : " + version;
                s += "\nApp version code : " + versionCode;
                s += "\n\n" + sw.toString();
                Intent startActivity = new Intent();
                startActivity.setClass(getBaseContext(), ExceptionSend.class);
                startActivity.setAction(ExceptionSend.class.getName());
                startActivity.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity.putExtra("StackTrace", s);
                getBaseContext().startActivity(startActivity);
                System.exit(0);
            }
        });
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferenceseditor = preferences.edit();
        if (!isMyServiceRunning(Notifs.class) && preferences.getBoolean("activnotif", true)) {
            startService(new Intent(getApplicationContext(), Notifs.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
