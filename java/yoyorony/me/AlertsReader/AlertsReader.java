package yoyorony.me.AlertsReader;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AlertsReader {
    public static ArrayList<Alert> ReadAlerts(String donnees) throws IOException{
        //connexion
        URL url = new URL("https://community.funcraft.net/login/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(6000);
        connection.setConnectTimeout(6000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestMethod("POST");
        connection.setInstanceFollowRedirects(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(donnees);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String ligne;
        String source = "";
        while ((ligne = reader.readLine()) != null) {
            source += ligne;
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }
        }

        connection.disconnect();

        //traitement
        ArrayList<Alert> alerts = new ArrayList<>();

        while(source.contains("<li id=\"alert")){
            source = source.substring(source.indexOf("<li id=\"alert") +13);
            String alertstring = source.substring(0, source.indexOf("</li>"));
            Alert alert = new Alert();

            alert.setType(findType(alertstring));
            alert.setLink(findLink(alertstring, alert.getType()));
            alert.setWho(findWho(alertstring, alert.getType()));
            alert.setWhere(findWhere(alertstring, alert.getType()));
            alert.setNew(findNew(alertstring));

            alerts.add(alert);
        }

        return alerts;
    }

    public static int findType(String str){
        if(str.contains("a répondu à la discussion intitulée")){return 0;}
        if(str.contains("a joint un fichier dans la discussion")){return 1;}
        if(str.contains("a cité votre message dans la discussion")){return 2;}
        if(str.contains("vous a mentionné dans un message de la discussion")){return 3;}
        if(str.contains("a aimé votre message dans la discussion")){return 4;}
        if(str.contains("a écrit un message sur") && str.contains("votre profil")){return 5;}
        if(str.contains("a émis un commentaire sur") && str.contains("votre statut")){return 6;} if(str.contains("a émis un commentaire sur un message de") && str.contains("sur votre profil")){return 6;}
        if(str.contains("a émis un commentaire concernant") && str.contains("votre message") && str.contains("écrit sur le profil de")){return 7;}
        if(str.contains("a également émis un commentaire sur un message de profil de")){return 8;}
        if(str.contains("vous a mentionné dans un") && str.contains("sur le profil")){return 9;}
        if(str.contains("????")){return 10;} //on ne saura jamais
        if(str.contains("????")){return 11;} //on ne saura jamais non plus
        if(str.contains("vous suit dorénavant")){return 12;}
        if(str.contains("avez reçu un trophée")){return 13;}
        if(str.contains("a commencé une discussion nommée")){return 14;}
        return 0;
    }

    public static String findLink(String str, int type){
        switch (type){
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 14:
                str = str.substring(str.indexOf("<a")+1);
                str = str.substring(str.indexOf("<a")+1);
                str = str.substring(str.indexOf("<a href=\"") + 9);
                return "https://community.funcraft.net/" + str.substring(0, str.indexOf("\""));
            case 12: case 13:
                str = str.substring(str.indexOf("<a")+1);
                str = str.substring(str.indexOf("<a href=\"") + 9);
                return "https://community.funcraft.net/" + str.substring(0, str.indexOf("\""));
        }
        return "";
    }

    public static String findWho(String str, int type){
        switch (type){
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12: case 14:
                str = str.substring(str.indexOf("dir=\"auto\">") + 11);
                return str.substring(0, str.indexOf("<"));
            case 13:
                return "Vous";
        }
        return "";
    }

    public static String findWhere(String str, int type){
        switch (type){
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 14:
                str = str.substring(str.indexOf("class=\"PopupItemLink\">") + 22);
                str = str.substring(0, str.indexOf("</a>"));
                if(str.contains("<span")){
                    str = str.substring(str.indexOf("</span>") + 8); //+1 espace
                }
                return Jsoup.parse(str).text();
            case 12:
                return "Vous suit dorénavant";
            case 13:
                str = str.substring(str.indexOf("class=\"PopupItemLink OverlayTrigger\">") + 37);
                str = str.substring(0, str.indexOf("</a>"));
                if(str.contains("<span")){
                    str = str.substring(str.indexOf("</span>") + 8); //+1 espace
                }
                return Jsoup.parse(str).text();
        }
        return "";
    }

    public static ArrayList<Convo> ReadConvos(String donnees) throws IOException{
        //connexion
        URL url = new URL("https://community.funcraft.net/login/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10000);
        connection.setInstanceFollowRedirects(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(donnees);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String ligne;
        String source = "";
        while ((ligne = reader.readLine()) != null) {
            source += ligne;
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }
        }

        connection.disconnect();

        //traitement
        ArrayList<Convo> convos = new ArrayList<>();

        while(source.contains("<li id=\"conversation")){
            source = source.substring(source.indexOf("<li id=\"conversation") +20);
            String convostring = source.substring(0, source.indexOf("</li>"));
            Convo convo = new Convo();

            convo.setTitle(findTitle(convostring));
            convo.setLastGuy(findLastGuy(convostring));
            convo.setPubDateMessage(findPubDate(convostring));
            convo.setLue(findLue(convostring));
            convo.setLink(findLink(convostring));

            convos.add(convo);
        }

        return convos;
    }

    public static String findTitle(String str){
        str = str.substring(str.indexOf("<a href=\"conversations"));
        str = str.substring(str.indexOf(">")+1);
        return Jsoup.parse(str.substring(0, str.indexOf("<"))).text();
    }

    public static String findPubDate(String str){
        if(str.contains("<abbr class=\"DateTime\"")){
            str = str.substring(str.indexOf("<abbr class=\"DateTime\""));
            str = str.substring(str.indexOf(">")+1);
            return "Le" + str.substring(0, str.indexOf("<"));
        }
        str = str.substring(str.indexOf("<span class=\"DateTime\""));
        str = str.substring(str.indexOf(">")+1);
        return "Le " + str.substring(0, str.indexOf("<"));
    }

    public static String findLastGuy(String str){
        str = str.substring(str.indexOf("<div class=\"listBlock lastPost\">"));
        str = str.substring(str.indexOf("<a href=\"members"));
        str = str.substring(str.indexOf(">")+1);
        return "Dernier message :\nDe " + str.substring(0, str.indexOf("<"));
    }

    public static boolean findLue(String str){
        return !str.contains("unread");
    }

    public static String findLink(String str){
        str = str.substring(str.indexOf("<div class=\"listBlock lastPost\">"));
        str = str.substring(str.indexOf("<dd class=\"muted\">"));
        str = str.substring(str.indexOf("<a href=\"")+9);
        return "https://community.funcraft.net/" + str.substring(0, str.indexOf("\""));
    }

    public static boolean findNew(String str){
        return str.contains("<span class=\"newIcon\"></span>");
    }
}
