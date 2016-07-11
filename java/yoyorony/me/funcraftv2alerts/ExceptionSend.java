package yoyorony.me.funcraftv2alerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ExceptionSend extends Activity {
    public static boolean logRapportFinish = false;

    public static void sendRapport(final String exceptionStackTrace) {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        URL url = new URL("http://serveur24sur24.free.fr/FuncraftAlerts/rapportwriter.php?m=" + URLEncoder.encode(exceptionStackTrace, "UTF-8"));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setChunkedStreamingMode(0);
                        connection.setConnectTimeout(4000);

                        connection.getResponseCode();

                        connection.disconnect();
                    } catch (IOException e) {
                        logRapportFinish = true;
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voidlayout);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ExceptionSend.this)
                .setTitle("Crash !")
                .setMessage("Oops, l'application a crashé...\nPouvez-vous, s'il vous plait, envoyer le rapport pour que je puisse corriger l'erreur ? Merci !\n\n(L'envoie peut prendre un peu de temps selon la connexion et peut utiliser le réseau mobile)")
                .setCancelable(false)
                .setPositiveButton("ENVOYER LE RAPPORT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Bundle extras = getIntent().getExtras();
                            String StackTrace = extras.getString("StackTrace");
                            sendRapport(StackTrace);
                            int security = 0;
                            while (!logRapportFinish && security < 50) { //securité = 6sec max
                                security++;
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ignored) {
                                }
                            }
                        } catch (NullPointerException ignored) {
                        }
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setNegativeButton("NE PAS ENVOYER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        alertDialogBuilder.create().show();
    }
}
