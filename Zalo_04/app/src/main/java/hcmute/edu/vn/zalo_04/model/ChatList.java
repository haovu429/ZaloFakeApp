package hcmute.edu.vn.zalo_04.model;

public class ChatList {
    private String id; // id chat của tài khoản đã chat trước đó của một tài khoản người dùng

    public ChatList(String id) {
        this.id = id;
    }

    public ChatList() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
