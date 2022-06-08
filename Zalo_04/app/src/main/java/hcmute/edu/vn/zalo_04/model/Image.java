package hcmute.edu.vn.zalo_04.model;

public class Image {
    private String id;
    private String sender;
    private String time;

    public Image(String id, String sender, String time) {
        this.id = id;
        this.sender = sender;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
