package hcmute.edu.vn.zalo_04.model;

public class Video {
    private String id; //id video
    private String sender; // id người gửi video
    private String receiver; // id người nhận video
    private String time; //Thời gian gửi video
    private String filename; //Tên file video trên storage

    public Video(String id, String sender, String receiver, String time, String filename) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.filename = filename;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
