package yoyorony.me.PostsManager;

public class ConvoPost {
    private String Who = "";
    private String Message = "";
    private String PubDate = "";
    private int PostCode = -1;
    private int ConvoCode = -1;

    public ConvoPost(){}

    public String getWho() {return Who;}
    public String getMessage() {return Message;}
    public String getPubDate() {return PubDate;}
    public int getPostCode() {return PostCode;}
    public int getConvoCode() {return ConvoCode;}

    public void setWho(String who) {Who = who;}
    public void setMessage(String message) {Message = message;}
    public void setPubDate(String pubDate) {PubDate = pubDate;}
    public void setPostCode(int postCode) {PostCode = postCode;}
    public void setConvoCode(int convoCode) {ConvoCode = convoCode;}

    public String getViewLink(){return "https://community.funcraft.net/conversations/" + String.valueOf(ConvoCode) + "/message?message_id=" + String.valueOf(PostCode);}
    public String getReportLink(){return "https://community.funcraft.net/conversations/" + String.valueOf(ConvoCode) + "/report?message_id=" + String.valueOf(PostCode);}
    public String getEditLink(){return "https://community.funcraft.net/conversations/" + String.valueOf(ConvoCode) + "/edit-message?m=" + String.valueOf(PostCode);}
}
