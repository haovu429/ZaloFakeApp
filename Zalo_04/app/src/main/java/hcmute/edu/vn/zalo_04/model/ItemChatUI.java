package hcmute.edu.vn.zalo_04.model;

public class ItemChatUI {
    private Integer id;
    private Integer img_id;
    private String title;
    private String last_time;
    private String last_message;

    public ItemChatUI(Integer id, Integer img_id, String title) {
        this.id = id;
        this.img_id = img_id;
        this.title = title;
    }

    public ItemChatUI(Integer img_id, String title) {
        this.img_id = img_id;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImg_id() {
        return img_id;
    }

    public void setImg_id(Integer img_id) {
        this.img_id = img_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
