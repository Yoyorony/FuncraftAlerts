package yoyorony.me.funcraftv2alerts;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import yoyorony.me.RSSReader.Feed;
import yoyorony.me.RSSReader.Item;
import yoyorony.me.RSSReader.RSSReader;

public class Connexion {
    public static void testConnexion(final String[][] messages) {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                    try {
                        final String donnees = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(MainActivity.name.getText().toString(), "UTF-8")
                                + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(MainActivity.mdp.getText().toString(), "UTF-8")
                                + "&" + URLEncoder.encode("cookie_check", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8")
                                + "&" + URLEncoder.encode("_xfToken", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
                                + "&" + URLEncoder.encode("redirect", "UTF-8") + "=" + URLEncoder.encode("https://community.funcraft.net/", "UTF-8");

                        new Thread(new Runnable() {
                            public void run() {
                                Looper.prepare();
                                try {
                                    //connexion
                                    URL url = new URL("https://community.funcraft.net/login/login");
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoOutput(true);
                                    connection.setChunkedStreamingMode(0);
                                    connection.setRequestMethod("POST");
                                    connection.setConnectTimeout(10000);
                                    connection.setInstanceFollowRedirects(false);

                                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                                    writer.write(donnees);
                                    writer.flush();

                                    String sessionid = null;
                                    String headerName;
                                    for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                                        if (headerName.equals("Set-Cookie")) {
                                            String cookie = connection.getHeaderField(i);
                                            cookie = cookie.substring(0, cookie.indexOf(";"));
                                            String cookieName = cookie.substring(0, cookie.indexOf("="));
                                            String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                                            if (cookieName.equals("xf_session")) {
                                                sessionid = cookieValue;
                                            }
                                        }
                                    }

                                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                    String ligne;
                                    int result = -1;
                                    while ((ligne = reader.readLine()) != null) {
                                        if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                                            result = 0;
                                            break;
                                        }
                                    }

                                    connection.disconnect();


                                    //verification
                                    url = new URL("https://community.funcraft.net/");
                                    connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoOutput(true);
                                    connection.setConnectTimeout(10000);
                                    connection.setRequestProperty("Cookie", "xf_session=" + sessionid);

                                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                    while ((ligne = reader.readLine()) != null) {
                                        if (ligne.contains("<strong class=\"accountUsername\">" + MainActivity.name.getText().toString() + "</strong>")) {
                                            result = 1;
                                            break;
                                        }
                                    }

                                    connection.disconnect();

                                    switch (result) { //-1: unknown error   0: invalid ids   1: good ids
                                        case -1:
                                            sendMessage(messages[4][0], messages[4][1]);
                                            Connexion.succes(0);
                                            break;
                                        case 0:
                                            sendMessage(messages[5][0], messages[5][1]);
                                            Connexion.succes(-1);
                                            break;
                                        case 1:
                                            sendMessage(messages[6][0], messages[6][1]);
                                            Connexion.succes(1);
                                            break;
                                    }
                                } catch (IOException e) {
                                    if (e instanceof SocketTimeoutException) {
                                        sendMessage(messages[7][0], messages[7][1]);
                                        Connexion.succes(0);
                                    } else {
                                        sendMessage(messages[2][0], messages[2][1]);
                                        Connexion.succes(0);
                                    }
                                }
                                Looper.loop();
                            }
                        }).start();
                    } catch (IOException e) {
                        sendMessage(messages[3][0], messages[3][1]);
                        Connexion.succes(-1);
                    }
                } else {
                    sendMessage(messages[8][0], messages[8][1]);
                    Connexion.succes(0);
                }
            } else {
                sendMessage(messages[0][0], messages[0][1]);
                Connexion.succes(0);
            }
        } else {
            sendMessage(messages[1][0], messages[1][1]);
            Connexion.succes(0);
        }
    }

    public static void refreshAlerts() {
        boolean opti = FunApp.preferences.getBoolean("opti", true);
        if (!opti) {
            NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                    boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                    if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                        try {
                            final String donnees = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(FunApp.preferences.getString("name", ""), "UTF-8")
                                    + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(FunApp.preferences.getString("mdp", ""), "UTF-8")
                                    + "&" + URLEncoder.encode("cookie_check", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8")
                                    + "&" + URLEncoder.encode("_xfToken", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8")
                                    + "&" + URLEncoder.encode("redirect", "UTF-8") + "=" + URLEncoder.encode("https://community.funcraft.net/", "UTF-8");

                            new Thread(new Runnable() {
                                public void run() {
                                    Looper.prepare();
                                    try {
                                        //connexion
                                        URL url = new URL("https://community.funcraft.net/login/login");
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoOutput(true);
                                        connection.setChunkedStreamingMode(0);
                                        connection.setRequestMethod("POST");
                                        connection.setConnectTimeout(10000);
                                        connection.setInstanceFollowRedirects(false);

                                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                                        writer.write(donnees);
                                        writer.flush();

                                        String sessionid = null;
                                        String headerName;
                                        for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                                            if (headerName.equals("Set-Cookie")) {
                                                String cookie = connection.getHeaderField(i);
                                                cookie = cookie.substring(0, cookie.indexOf(";"));
                                                String cookieName = cookie.substring(0, cookie.indexOf("="));
                                                String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                                                if (cookieName.equals("xf_session")) {
                                                    sessionid = cookieValue;
                                                }
                                            }
                                        }

                                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                        String ligne;
                                        while ((ligne = reader.readLine()) != null) {
                                            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                                                break;
                                            }
                                        }

                                        connection.disconnect();


                                        //verification
                                        url = new URL("https://community.funcraft.net/");
                                        connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoOutput(true);
                                        connection.setConnectTimeout(10000);
                                        connection.setRequestProperty("Cookie", "xf_session=" + sessionid);

                                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                        int turn = 0;
                                        while ((ligne = reader.readLine()) != null) {
                                            if (ligne.contains("<span class=\"Total\">")) {
                                                turn++;
                                                if (turn == 1) {
                                                    FunApp.alerts[0] = Integer.parseInt(ligne.replaceAll("[\\D]", ""));
                                                } else if (turn == 2) {
                                                    FunApp.alerts[1] = Integer.parseInt(ligne.replaceAll("[\\D]", ""));
                                                }
                                            }
                                        }

                                        connection.disconnect();
                                    } catch (IOException | NullPointerException ignored) {
                                    }
                                    Looper.loop();
                                }
                            }).start();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        } else {
            //TODO this ?????
        }
    }

    public static void checkVersion(final String[][] messages) {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                    new Thread(new Runnable() {
                        public void run() {
                            Looper.prepare();
                            try {
                                URL url = new URL("http://serveur24sur24.free.fr/FuncraftAlerts/currentversion.txt");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setConnectTimeout(10000);
                                connection.setChunkedStreamingMode(0);

                                int versionserv = 0, versionapp = BuildConfig.VERSION_CODE;
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                String ligne;
                                while ((ligne = reader.readLine()) != null) {
                                    if (!ligne.equals("")) {
                                        versionserv = Integer.parseInt(ligne.replaceAll("[\\D]", ""));
                                        break;
                                    }
                                }

                                if (versionapp < versionserv) {
                                    sendMessageMAJ(messages[3][0], messages[3][1]);
                                } else {
                                    sendMessage(messages[4][0], messages[4][1]);
                                }

                                connection.disconnect();

                                MainActivity.progressBarCheckversion.setIndeterminate(false);
                                MainActivity.progressBarCheckversion.setProgress(1);
                            } catch (IOException e) {
                                if (e instanceof SocketTimeoutException) {
                                    sendMessage(messages[5][0], messages[5][1]);
                                    MainActivity.progressBarCheckversion.setIndeterminate(false);
                                    MainActivity.progressBarCheckversion.setProgress(0);
                                } else {
                                    sendMessage(messages[2][0], messages[2][1]);
                                    MainActivity.progressBarCheckversion.setIndeterminate(false);
                                    MainActivity.progressBarCheckversion.setProgress(0);
                                }
                            }
                            Looper.loop();
                        }
                    }).start();
                } else {
                    sendMessage(messages[6][0], messages[6][1]);
                    MainActivity.progressBarCheckversion.setIndeterminate(false);
                    MainActivity.progressBarCheckversion.setProgress(0);
                }
            } else {
                sendMessage(messages[0][0], messages[0][1]);
                MainActivity.progressBarCheckversion.setIndeterminate(false);
                MainActivity.progressBarCheckversion.setProgress(0);
            }
        } else {
            sendMessage(messages[1][0], messages[1][1]);
            MainActivity.progressBarCheckversion.setIndeterminate(false);
            MainActivity.progressBarCheckversion.setProgress(0);
        }
    }

    public static void checkVersionSilence() {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                URL url = new URL("http://serveur24sur24.free.fr/FuncraftAlerts/currentversion.txt");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setConnectTimeout(10000);
                                connection.setChunkedStreamingMode(0);

                                int versionserv = 0, versionapp = BuildConfig.VERSION_CODE;
                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                String ligne;
                                while ((ligne = reader.readLine()) != null) {
                                    if (!ligne.equals("")) {
                                        versionserv = Integer.parseInt(ligne.replaceAll("[\\D]", ""));
                                        break;
                                    }
                                }

                                FunApp.majdispo = versionapp < versionserv;

                                connection.disconnect();
                            } catch (IOException ignored) {
                            }
                            AutoMAJ.reponsed = true;
                        }
                    }).start();
                }
            }
        }
    }

    private static void sendMessage(String m1, String m2) {
        Message m = Message.obtain();
        m.obj = m1 + ":777:" + m2;
        MainActivity.ConnectHandler.sendMessage(m);
    }

    private static void sendMessageMAJ(String m1, String m2) {
        Message m = Message.obtain();
        m.obj = m1 + ":777:" + m2;
        MainActivity.MAJHandler.sendMessage(m);
    }

    private static void succes(int succes) {
        if (succes == 1) {
            MainActivity.progressBarConnect.setIndeterminate(false);
            MainActivity.progressBarConnect.setProgress(1);
            FunApp.preferenceseditor.putInt("connectedProgress", 1);
            FunApp.preferenceseditor.putString("name", MainActivity.name.getText().toString());
            FunApp.preferenceseditor.putString("mdp", MainActivity.mdp.getText().toString());
            FunApp.preferenceseditor.commit();
        } else if (succes == -1) {
            MainActivity.progressBarConnect.setIndeterminate(false);
            MainActivity.progressBarConnect.setProgress(0);
            FunApp.preferenceseditor.putInt("connectedProgress", 0);
            FunApp.preferenceseditor.putString("name", "");
            FunApp.preferenceseditor.putString("mdp", "");
            FunApp.preferenceseditor.commit();
        } else {
            MainActivity.progressBarConnect.setIndeterminate(false);
            MainActivity.progressBarConnect.setProgress(0);
        }
    }

    public static boolean isSupDeuxG(int subType) {
        switch (subType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return false;
        }
    }

    public static void getSubmenuSubtitles(final String[] urlstr, final int subint) {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future f = executor.submit(new Runnable() {
                        public void run() {
                            try {
                                ArrayList<String> Strings = new ArrayList<>();
                                for (String anUrlstr : urlstr) {
                                    Feed feed = RSSReader.ReadRSS(new URL(anUrlstr));
                                    Strings.add(feed.getDescription());
                                }
                                if(subint == 0){
                                    ForumActivitySubmenu.Subtitle = Strings;
                                    ForumActivitySubmenu.loaded = true;
                                }else if(subint == 1) {
                                    ForumActivityItems.SubtitleOption = Strings;
                                    ForumActivityItems.loadedOption = true;
                                }else if(subint == 2) {
                                    ForumActivityItemsSecond.SubtitleOption = Strings;
                                    ForumActivityItemsSecond.loadedOption = true;
                                }
                            } catch (IOException e) {
                                ArrayList<String> error = new ArrayList<>();
                                for (String ignored : urlstr) {
                                    error.add("Erreur réseau !");
                                }
                                if (subint == 0) {
                                    ForumActivitySubmenu.Subtitle = error;
                                    ForumActivitySubmenu.loaded = true;
                                }else if(subint == 1) {
                                    ForumActivityItems.SubtitleOption = error;
                                    ForumActivityItems.loadedOption = true;
                                }else if(subint == 2) {
                                    ForumActivityItemsSecond.SubtitleOption = error;
                                    ForumActivityItemsSecond.loadedOption = true;
                                }
                            }
                        }
                    });
                    try {
                        f.get(6000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | TimeoutException | ExecutionException e) {
                        f.cancel(true);
                        ArrayList<String> error = new ArrayList<>();
                        for (String ignored : urlstr) {
                            error.add("Erreur réseau !");
                        }
                        if (subint == 0) {
                            ForumActivitySubmenu.Subtitle = error;
                            ForumActivitySubmenu.loaded = true;
                        }else if(subint == 1) {
                            ForumActivityItems.SubtitleOption = error;
                            ForumActivityItems.loadedOption = true;
                        }else if(subint == 2) {
                            ForumActivityItemsSecond.SubtitleOption = error;
                            ForumActivityItemsSecond.loadedOption = true;
                        }
                    }
                }
            }
        }
    }

    public static void getItemsFeed(final String urlstr, final int subint) {
        NetworkInfo networkInfo = FunApp.connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            if (wifi || !FunApp.preferences.getBoolean("wifionly", true)) {
                boolean supDeuxG = isSupDeuxG(networkInfo.getSubtype());
                if (supDeuxG || FunApp.preferences.getBoolean("deuxG", false) || wifi) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future f = executor.submit(new Runnable() {
                        public void run() {
                            try {
                                Feed feed = RSSReader.ReadRSS(new URL(urlstr));
                                ArrayList<String> Strings1 = new ArrayList<>();
                                ArrayList<String> Strings2 = new ArrayList<>();
                                ArrayList<String> Strings3 = new ArrayList<>();
                                ArrayList<String> Strings4 = new ArrayList<>();
                                for (Item item : feed.getItems()) {
                                    Strings1.add(item.getTitle());
                                    Strings2.add(item.getContent());
                                    Strings3.add(item.getFromPubDate());
                                    Strings4.add(item.getLink());
                                }

                                if(subint == 0) {
                                    ForumActivityItems.Title = Strings1;
                                    ForumActivityItems.Subtitle = Strings2;
                                    ForumActivityItems.Dates = Strings3;
                                    ForumActivityItems.Links = Strings4;
                                    ForumActivityItems.loaded = true;
                                }else if(subint == 1){
                                    ForumActivityItemsSecond.Title = Strings1;
                                    ForumActivityItemsSecond.Subtitle = Strings2;
                                    ForumActivityItemsSecond.Dates = Strings3;
                                    ForumActivityItemsSecond.Links = Strings4;
                                    ForumActivityItemsSecond.loaded = true;
                                }else if(subint == 2){
                                    ForumActivityItemsThird.Title = Strings1;
                                    ForumActivityItemsThird.Subtitle = Strings2;
                                    ForumActivityItemsThird.Dates = Strings3;
                                    ForumActivityItemsThird.Links = Strings4;
                                    ForumActivityItemsThird.loaded = true;
                                }
                            } catch (IOException e) {
                                ArrayList<String> error = new ArrayList<>();
                                error.add("Erreur réseau !");
                                ArrayList<String> errorlink = new ArrayList<>();
                                errorlink.add("");

                                if(subint == 0){
                                    ForumActivityItems.Title = error;
                                    ForumActivityItems.Subtitle = error;
                                    ForumActivityItems.Dates = error;
                                    ForumActivityItems.Links = errorlink;
                                    ForumActivityItems.loaded = true;
                                }else if(subint == 1){
                                    ForumActivityItemsSecond.Title = error;
                                    ForumActivityItemsSecond.Subtitle = error;
                                    ForumActivityItemsSecond.Dates = error;
                                    ForumActivityItemsSecond.Links = errorlink;
                                    ForumActivityItemsSecond.loaded = true;
                                }else if(subint == 2){
                                    ForumActivityItemsThird.Title = error;
                                    ForumActivityItemsThird.Subtitle = error;
                                    ForumActivityItemsThird.Dates = error;
                                    ForumActivityItemsThird.Links = errorlink;
                                    ForumActivityItemsThird.loaded = true;
                                }
                            }
                        }
                    });
                    try {
                        f.get(6000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | TimeoutException | ExecutionException e) {
                        f.cancel(true);
                        ArrayList<String> error = new ArrayList<>();
                        error.add("Erreur réseau !");
                        ArrayList<String> errorlink = new ArrayList<>();
                        errorlink.add("");

                        if(subint == 0){
                            ForumActivityItems.Title = error;
                            ForumActivityItems.Subtitle = error;
                            ForumActivityItems.Dates = error;
                            ForumActivityItems.Links = errorlink;
                            ForumActivityItems.loaded = true;
                        }else if(subint == 1){
                            ForumActivityItemsSecond.Title = error;
                            ForumActivityItemsSecond.Subtitle = error;
                            ForumActivityItemsSecond.Dates = error;
                            ForumActivityItemsSecond.Links = errorlink;
                            ForumActivityItemsSecond.loaded = true;
                        }else if(subint == 2){
                            ForumActivityItemsThird.Title = error;
                            ForumActivityItemsThird.Subtitle = error;
                            ForumActivityItemsThird.Dates = error;
                            ForumActivityItemsThird.Links = errorlink;
                            ForumActivityItemsThird.loaded = true;
                        }
                    }
                }
            }
        }
    }
}
