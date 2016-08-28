package yoyorony.me.PostsManager;

public class ConvoPost {
    private String Who = "";
    private String Message = "";
    private String PubDate = "";
    private String PostCode = "-1";
    private String ConvoCode = "-1";
    private String MemberCode = "-1";
    private boolean CanEdited = false;

    public ConvoPost(){}

    public String getWho() {return Who;}
    public String getMessage() {return Message;}
    public String getPubDate() {return PubDate;}
    public boolean getCanEdited() {return CanEdited;}

    public void setWho(String who) {Who = who;}
    public void setMessage(String message) {Message = message;}
    public void setPubDate(String pubDate) {PubDate = pubDate;}
    public void setPostCode(String postCode) {PostCode = postCode;}
    public void setConvoCode(String convoCode) {ConvoCode = convoCode;}
    public void setCanEdited(boolean canEdited) {CanEdited = canEdited;}
    public void setMemberCode(String memberCode) {MemberCode = memberCode;}

    public String getViewLink(){return "https://community.funcraft.net/conversations/" + ConvoCode + "/message?message_id=" + PostCode;}
    public String getReportLink(){return "https://community.funcraft.net/conversations/" + ConvoCode + "/report?message_id=" + PostCode;}
    public String getEditLink(){return "https://community.funcraft.net/conversations/" + ConvoCode + "/edit-message?m=" + PostCode;}
    public String getQuoteParam(){return Who + ", convMessage: " + PostCode + ", member: " + MemberCode;}
}
