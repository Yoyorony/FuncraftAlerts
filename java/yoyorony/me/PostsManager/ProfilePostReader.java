package yoyorony.me.PostsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfilePostReader {
    public static ProfilePost ReadProfilePost(String donnees, String postCode) throws IOException {
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
            if (ligne.contains("<li id=\"profile-post-" + postCode)) {rec = true;}
            if (rec) {source += ligne;}
            if (rec && ligne.contains("<ol class=\"messageResponse\">")) {break;}
            if (ligne.contains("Pseudonyme ou mot de passe invalide !")) {
                source = "";
                break;
            }
        }

        connection.disconnect();

        //traitement
        ProfilePost profilePost = new ProfilePost();

        profilePost.setWho(findWho(source));
        profilePost.setMessage(findMessage(source));
        profilePost.setPubDate(findPubDate(source));
        profilePost.setPostCode(Integer.parseInt(postCode));

        return profilePost;
    }

    public static String findWho(String str){
        str = str.substring(str.indexOf("data-author=\"") + 13);
        return str.substring(0, str.indexOf("\">"));
    }

    public static String findMessage(String str){
        str = str.substring(str.indexOf("<blockquote"));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</blockquote>"));
    }

    public static String findPubDate(String str){
        str = str.substring(str.indexOf("<abbr class=\"DateTime\""));
        str = str.substring(str.indexOf(">") +1);
        return str.substring(0, str.indexOf("</abbr>"));
    }
}
