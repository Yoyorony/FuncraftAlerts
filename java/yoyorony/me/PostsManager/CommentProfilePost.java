package yoyorony.me.PostsManager;

public class CommentProfilePost {

    private String Who = "";
    private String Message = "";
    private String PubDate = "";
    private int PostCode = -1;

    public CommentProfilePost(){}

    public String getWho() {return Who;}
    public String getMessage() {return Message;}
    public String getPubDate() {return PubDate;}
    public int getPostCode() {return PostCode;}

    public void setWho(String who) {Who = who;}
    public void setMessage(String message) {Message = message;}
    public void setPubDate(String pubDate) {PubDate = pubDate;}
    public void setPostCode(int postCode) {PostCode = postCode;}

    public String getViewLink(){return "https://community.funcraft.net/profile-posts/comments/" + String.valueOf(PostCode);}
    public String getEditLink(){return "https://community.funcraft.net/profile-posts/comments/" + String.valueOf(PostCode) + "/edit";}
    public String getDeleteLink(){return "https://community.funcraft.net/profile-posts/comments/" + String.valueOf(PostCode) + "/delete";}
    public String getReportLink(){return "https://community.funcraft.net/profile-posts/comments/" + String.valueOf(PostCode) + "/report";}
}
