package yoyorony.me.AlertsReader;

import java.util.ArrayList;

public class Convo {
    private String Title = "";
    private String PubDateMessage = "";
    private String LastGuy = "";
    private boolean Lue = false;
    private String Link = "";

    public Convo(){}

    public void setTitle(String s){Title = s;}
    public void setPubDateMessage(String s){PubDateMessage = s;}
    public void setLastGuy(String s){LastGuy = s;}
    public void setLue(boolean b){Lue = b;}
    public void setLink(String s) {Link = s;}

    public String getTitle(){return Title;}
    public String getPubDateMessage(){return PubDateMessage;}
    public String getLastGuy(){return LastGuy;}
    public boolean isLue(){return Lue;}
    public String getLink() {return Link;}
}
