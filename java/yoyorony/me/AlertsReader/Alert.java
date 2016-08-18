package yoyorony.me.AlertsReader;

public class Alert {
    private int Type = 0; //respecte l'ordre dans "alert-preferences" (0: reponse discution   1: joint un fichier   etc...)
    private String Link = "";
    private String Who = "";
    private String Where = "";
    private boolean New = false;

    public Alert(){}

    public int getType() {return Type;}
    public String getLink() {return Link;}
    public String getWho() {return Who;}
    public String getWhere() {return Where;}
    public boolean getNew() {return New;}

    public void setType(int type) {Type = type;}
    public void setLink(String link) {Link = link;}
    public void setWho(String who) {Who = who;}
    public void setWhere(String where) {Where = where;}
    public void setNew(boolean neww) {New = neww;}
}
