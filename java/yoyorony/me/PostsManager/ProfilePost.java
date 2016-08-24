package yoyorony.me.PostsManager;

public class ProfilePost {
    private String Who = "";
    private String Where = "";
    private String Message = "";
    private String PubDate = "";
    private int PostCode = -1;

    public ProfilePost(){}

    public String getWho() {return Who;}
    public String getWhere() {return Where;}
    public String getMessage() {return Message;}
    public String getPubDate() {return PubDate;}
    public int getPostCode() {return PostCode;}

    public void setWho(String who) {Who = who;}
    public void setWhere(String where) {Where = where;}
    public void setMessage(String message) {Message = message;}
    public void setPubDate(String pubDate) {PubDate = pubDate;}
    public void setPostCode(int postCode) {PostCode = postCode;}

    public String getViewLink(){return "https://community.funcraft.net/profile-posts/" + String.valueOf(PostCode);}
    public String getEditLink(){return "https://community.funcraft.net/profile-posts/" + String.valueOf(PostCode) + "/edit";}
    public String getDeleteLink(){return "https://community.funcraft.net/profile-posts/" + String.valueOf(PostCode) + "/delete";}
    public String getReportLink(){return "https://community.funcraft.net/profile-posts/" + String.valueOf(PostCode) + "/report";}
    public String getCommentsLink(){return "https://community.funcraft.net/profile-posts/" + String.valueOf(PostCode) + "/comments";}
}
