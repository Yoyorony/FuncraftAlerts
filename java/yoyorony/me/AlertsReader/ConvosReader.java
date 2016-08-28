package yoyorony.me.AlertsReader;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ConvosReader {
    public static ArrayList<Convo> ReadConvos(String donnees) throws IOException {
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
        boolean rec = false;
        while ((ligne = reader.readLine()) != null) {
            if(ligne.contains("<ol class=\"discussionListItems\">")){rec = true;}
            if(rec){source += ligne;}
            if(rec && ligne.contains("</ol>")){break;}
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
        str = str.substring(str.indexOf("<dd class=\"muted\">"));
        if(str.contains("<abbr class=\"DateTime\"")){
            str = str.substring(str.indexOf("<abbr class=\"DateTime\""));
            str = str.substring(str.indexOf(">")+1);
            return "Le " + str.substring(0, str.indexOf("<"));
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
}
