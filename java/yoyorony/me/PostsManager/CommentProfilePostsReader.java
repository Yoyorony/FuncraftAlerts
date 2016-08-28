package yoyorony.me.PostsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CommentProfilePostsReader {
    public static ArrayList<CommentProfilePost> ReadCommentProfilePosts(String donnees) throws IOException {
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
            if(ligne.contains("<ol class=\"messageSimple\">")){rec = true;}
            if(rec){source += ligne;}
            if(rec && ligne.contains("</ol>")){break;}
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }
        }

        connection.disconnect();

        //traitement
        ArrayList<CommentProfilePost> commentProfilePosts = new ArrayList<>();

        while(source.contains("<li id=\"profile-post-comment-")){
            source = source.substring(source.indexOf("<li id=\"profile-post-comment-") + 1);
            String poststring = source.substring(0, source.indexOf("</li>"));
            CommentProfilePost commentProfilePost = new CommentProfilePost();

            commentProfilePost.setWho(findWho(poststring));
            commentProfilePost.setMessage(findMessage(poststring));
            commentProfilePost.setPubDate(findPubDate(poststring));
            commentProfilePost.setPostCode(findPostCode(poststring));

            commentProfilePosts.add(commentProfilePost);
        }

        return commentProfilePosts;
    }

    public static String findWho(String str){
        str = str.substring(str.indexOf("<div class=\"commentContent\">"));
        str = str.substring(str.indexOf("<a href=\"members/"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</a>"));
    }

    public static String findMessage(String str){
        str = str.substring(str.indexOf("<blockquote"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</blockquote>"));
    }

    public static String findPubDate(String str){
        str = str.substring(str.indexOf("<span class=\"DateTime muted\""));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</span>"));
    }

    public static int findPostCode(String str){
        str = str.substring(str.indexOf("id=\"profile-post-comment-") +25);
        str = str.substring(0, str.indexOf("\" class="));
        return Integer.parseInt(str);
    }
}
