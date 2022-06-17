package hcmute.edu.vn.zalo_04.model;

public class Audio {
    private String id; //Id audio
    private String sender; //id người gửi audio
    private String receiver; //id người nhận audio
    private String time; //Thời gian gửi audio
    private String filename; //Tên file audio có đuôi mở rộng

    public Audio(String id, String sender, String receiver, String time, String filename) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.filename = filename;
    }

    public Audio() {
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
