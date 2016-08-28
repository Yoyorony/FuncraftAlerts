package yoyorony.me.PostsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ThreadPostsReader {
    public static ArrayList<ThreadPost> ReadThreadPosts(String donnees) throws IOException {
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
            if(ligne.contains("<ol class=\"messageList\" id=\"messageList\">")){rec = true;}
            if(rec){source += ligne;}
            if(rec && ligne.contains("</ol>")){break;}
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }
        }

        connection.disconnect();

        //traitement
        ArrayList<ThreadPost> threadPosts = new ArrayList<>();

        while(source.contains("<li id=\"post-")){
            source = source.substring(source.indexOf("<li id=\"post-") + 1);
            String blockquote = source.substring(source.indexOf("<blockquote"), source.indexOf("</blockquote>") +13);
            String poststring = source.substring(0, source.indexOf("<blockquote"));
            source = source.substring(source.indexOf("</blockquote>") +13, source.length());
            poststring += blockquote + source.substring(source.indexOf("</li>"));
            ThreadPost threadPost = new ThreadPost();

            threadPost.setWho(findWho(poststring));
            threadPost.setMessage(findMessage(poststring));
            threadPost.setPubDate(findPubDate(poststring));
            threadPost.setLiked(findLiked(poststring));
            threadPost.setPostCode(findPostCode(poststring));

            threadPosts.add(threadPost);
        }

        return threadPosts;
    }

    public static String findWho(String str){
        str = str.substring(str.indexOf("<div class=\"uix_usernameWrapper\">"));
        str = str.substring(str.indexOf("<a href=\"members/"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("<"));
    }

    public static String findMessage(String str){
        str = str.substring(str.indexOf("<blockquote"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</blockquote>"));
    }

    public static String findPubDate(String str){
        str = str.substring(str.indexOf("<abbr class=\"DateTime\""));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("<"));
    }

    public static boolean findLiked(String str){
        str = str.substring(str.indexOf("<div class=\"publicControls\">"), str.indexOf("<span class=\"LikeLabel\">"));
        return str.contains("unlike");
    }

    public static int findPostCode(String str){
        str = str.substring(str.indexOf("post-") + 5, str.indexOf("\" class="));
        return Integer.parseInt(str);
    }
}
