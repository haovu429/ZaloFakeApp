package hcmute.edu.vn.zalo_04.model;

public class Chat {

    private String sender; //id người gửi audio
    private String receiver; //id người nhận audio
    private String message = "None"; //Nội dung dạng tin nhắn text
    private String video = "None"; //Link video trên storage
    private String audio = "None"; // Link audio trên storage
    private String image = "None"; //Link hình ảnh trên storage
    private String time; // Thời gian gửi tin nhắn
    private boolean isseen; //Trạng thái xem tin nhắn của người nhận
    private String idfile = "None"; //Tên file trên storage nếu tin nhắn là dạng video, audio, image

    public Chat(String sender, String receiver, String message, String video, String audio, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.video = video;
        this.audio = audio;
        this.isseen = isseen;
    }

    public Chat(String sender, String receiver, String message, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
    }

    public Chat(String sender, String receiver, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.isseen = isseen;
    }

    public Chat() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdfile() {
        return idfile;
    }

    public void setIdfile(String idfile) {
        this.idfile = idfile;
    }
}
