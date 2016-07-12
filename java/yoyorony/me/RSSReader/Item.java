package yoyorony.me.RSSReader;

import android.text.Html;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.jsoup.Jsoup;

public class Item {
    private String title = "";
    private String pubDate = "";
    private String link = "";
    private String guid = "";
    private String author = "";
    private String creator = "";
    private String content = "";

    public Item() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getFromPubDate() {
        String[] S = pubDate.split(" ");
        int day = Integer.parseInt(S[1]);
        int month = 1;
        switch (S[2]) {
            case "Jan":
                month = 1;
                break;
            case "Feb":
                month = 2;
                break;
            case "Mar":
                month = 3;
                break;
            case "Apr":
                month = 4;
                break;
            case "May":
                month = 5;
                break;
            case "Jun":
                month = 6;
                break;
            case "Jul":
                month = 7;
                break;
            case "Aug":
                month = 8;
                break;
            case "Sep":
                month = 9;
                break;
            case "Oct":
                month = 10;
                break;
            case "Nov":
                month = 11;
                break;
            case "Dec":
                month = 12;
                break;
        }
        int year = Integer.parseInt(S[3]);
        String[] SS = S[4].split(":");
        int hours = Integer.parseInt(SS[0]);
        int minutes = Integer.parseInt(SS[0]);
        int seconds = Integer.parseInt(SS[0]);

        DateTime pubdatejoda = new DateTime(year, month, day, hours, minutes, seconds);
        DateTime todayjoda = new DateTime();

        int minutesbetween = Minutes.minutesBetween(pubdatejoda, todayjoda).getMinutes();

        String rep = "Dernier message : ";
        if(minutesbetween < 1){
            rep += "il y a un instant";
        }else if(minutesbetween < 60){
            rep += "il y a " + String.valueOf(minutesbetween) + " minutes";
        }else if(minutesbetween < 1440){
            rep += "il y a " + String.valueOf(minutesbetween/60) + " heures";
        }else if(minutesbetween < 43200){
            rep += "il y a " + String.valueOf(minutesbetween/1440) + " jours";
        }else if(pubdatejoda.getYear() == todayjoda.getYear()){
            rep += "le " + String.valueOf(pubdatejoda.getDayOfMonth()) + " ";
            String mois = "";
            switch (pubdatejoda.getMonthOfYear()) {
                case 1:
                    mois = "janvier";
                    break;
                case 2:
                    mois = "février";
                    break;
                case 3:
                    mois = "mars";
                    break;
                case 4:
                    mois = "avril";
                    break;
                case 5:
                    mois = "mai";
                    break;
                case 6:
                    mois = "juin";
                    break;
                case 7:
                    mois = "juillet";
                    break;
                case 8:
                    mois = "août";
                    break;
                case 9:
                    mois = "septembre";
                    break;
                case 10:
                    mois = "octobre";
                    break;
                case 11:
                    mois = "novembre";
                    break;
                case 12:
                    mois = "décembre";
                    break;
            }
            rep += mois;
        }else{
            rep += "le " + String.valueOf(pubdatejoda.getDayOfMonth()) + " ";
            String mois = "";
            switch (pubdatejoda.getMonthOfYear()) {
                case 1:
                    mois = "janvier";
                    break;
                case 2:
                    mois = "février";
                    break;
                case 3:
                    mois = "mars";
                    break;
                case 4:
                    mois = "avril";
                    break;
                case 5:
                    mois = "mai";
                    break;
                case 6:
                    mois = "juin";
                    break;
                case 7:
                    mois = "juillet";
                    break;
                case 8:
                    mois = "août";
                    break;
                case 9:
                    mois = "septembre";
                    break;
                case 10:
                    mois = "octobre";
                    break;
                case 11:
                    mois = "novembre";
                    break;
                case 12:
                    mois = "décembre";
                    break;
            }
            rep += mois + " " + String.valueOf(pubdatejoda.getYear());
        }
        return rep;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContent() {
        return Html.fromHtml(Jsoup.parse(content).text().replaceAll("<.*?>", "")).toString();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
