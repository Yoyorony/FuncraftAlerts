package yoyorony.me.AlertsReader;

import java.util.ArrayList;

public class Convo {
    private String Title = "";
    private int Reponses = 0;
    private ArrayList<String> Participants = new ArrayList<>();
    private String PubDateMessage = "";
    private String LastGuy = "";

    public Convo(String arg0, int arg1, ArrayList<String> arg2, String arg3, String arg4){
        Title = arg0;
        Reponses = arg1;
        Participants = arg2;
        PubDateMessage = arg3;
        LastGuy = arg4;
    }

    public void setTitle(String s){Title = s;}
    public void setReponses(int i){Reponses = i;}
    public void setParticipants(ArrayList<String> S){Participants = S;}
    public void setPubDateMessage(String s){PubDateMessage = s;}
    public void setLastGuy(String s){LastGuy = s;}

    public String getTitle(){return Title;}
    public int getReponses(){return Reponses;}
    public ArrayList<String> getParticipants(){return Participants;}
    public String getPubDateMessage(){return PubDateMessage;}
    public String getLastGuy(){return LastGuy;}
}
