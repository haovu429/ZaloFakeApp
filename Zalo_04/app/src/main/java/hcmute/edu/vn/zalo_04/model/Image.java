package hcmute.edu.vn.zalo_04.model;

public class Image {
    private String id; //id hình ảnh
    private String sender; //id tài khoản người gửi
    private String receiver; // id tài khoản người nhận
    private String time; //Thời gian gửi ảnh
    private String filename; //Tên file trong storage firebase

    public Image(String id, String sender, String receiver, String time, String filename) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.filename = filename;
    }

    public Image() {
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
