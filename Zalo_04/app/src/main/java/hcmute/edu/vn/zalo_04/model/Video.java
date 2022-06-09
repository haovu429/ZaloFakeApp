package hcmute.edu.vn.zalo_04.model;

public class Video {
    private String id;
    private String sender;
    private String receiver;
    private String time;

    public Video(String id, String sender, String receiver, String time) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }

    public Video() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
