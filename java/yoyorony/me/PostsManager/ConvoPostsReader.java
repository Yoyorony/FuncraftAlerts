package yoyorony.me.PostsManager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import yoyorony.me.funcraftv2alerts.ConvoQuickReplyActivity;
import yoyorony.me.funcraftv2alerts.FunApp;

public class ConvoPostsReader {
    public static ArrayList<ConvoPost> ReadConvoPosts(String donnees, String convoCode) throws IOException {
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
            if(ligne.contains("<ol class=\"messageList withSidebar\"")){rec = true;}
            if(rec){source += ligne;}
            if(rec && ligne.contains("</ol>")){break;}
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }

            if(ligne.contains("_xfToken")){
                String str = ligne.substring(ligne.indexOf("value=\"") +7);
                ConvoQuickReplyActivity.xfToken = str.substring(0, str.indexOf("\""));
            }
        }

        connection.disconnect();

        //traitement
        ArrayList<ConvoPost> convoPosts = new ArrayList<>();

        while(source.contains("<li id=\"message-")){//TODO trouver un truc propore pour ça : (attention au listes et aux quotations)
            source = source.substring(source.indexOf("<li id=\"message-") + 1);
            String blockquote = source.substring(source.indexOf("<article>"), source.indexOf("</article>") +10);
            String poststring = source.substring(0, source.indexOf("<article>"));
            source = source.substring(source.indexOf("</article>") +10, source.length());
            poststring += blockquote + source.substring(0, source.indexOf("</li>"));
            ConvoPost convoPost = new ConvoPost();

            convoPost.setWho(findWho(poststring));
            convoPost.setMessage(findMessage(blockquote));
            convoPost.setPubDate(findPubDate(poststring));
            convoPost.setPostCode(findPostCode(poststring));
            convoPost.setConvoCode(convoCode);
            convoPost.setMemberCode(findMemberCode(poststring));
            convoPost.setCanEdited(findCanEdited(poststring, convoPost.getWho()));

            convoPosts.add(convoPost);
        }
        return convoPosts;
    }

    public static String findWho(String str){
        str = str.substring(str.indexOf("<div class=\"uix_usernameWrapper\">"));
        str = str.substring(str.indexOf("<a href=\"members/"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</a>"));
    }

    public static String findMessage(String str){
        str = str.substring(str.indexOf("<blockquote"));
        str = str.substring(str.indexOf(">") +1);
        str = str.substring(0, str.indexOf("<div class=\"messageTextEndMarker\">&nbsp;</div>"));
        str = str.replace("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", "");
        str = str.replace("\t\t\t\t\t", "");
        return str;
    }

    public static String findPubDate(String str){
        str = str.substring(str.indexOf("<span class=\"item muted\">"));
        if(str.contains("<abbr class=\"DateTime\"")){
            str = str.substring(str.indexOf("<abbr class=\"DateTime\""));
            str = str.substring(str.indexOf(">")+1);
            return "Le " + str.substring(0, str.indexOf("<"));
        }
        str = str.substring(str.indexOf("<span class=\"DateTime\""));
        str = str.substring(str.indexOf(">")+1);
        return "Le " + str.substring(0, str.indexOf("<"));
    }

    public static String findPostCode(String str){
        str = str.substring(str.indexOf("id=\"message-") +12);
        return str.substring(0, str.indexOf("\" class="));
    }

    public static String findMemberCode(String str){
        str = str.substring(str.indexOf("<a href=\"members/"));
        str = str.substring(str.indexOf(".") +1);
        return str.substring(0, str.indexOf("/"));
    }

    public static boolean findCanEdited(String str, String who){
        if(who.equals(FunApp.preferences.getString("name", ""))){
            if(str.contains("edit-message?m=")){
                return true;
            }
        }
        return false;
    }
}
